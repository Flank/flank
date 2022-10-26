package flank.scripts.ops.firebase

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.common.config.flankGcloudCLIRepository
import flank.common.config.updateDependenciesWorkflowFilename
import flank.common.config.updatesOpenedUser
import flank.common.currentPath
import flank.common.downloadFile
import flank.scripts.data.github.commons.getLastWorkflowRunDate
import flank.scripts.data.github.getGitHubIssueList
import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.ops.firebase.common.createEpicIssue
import flank.scripts.ops.firebase.common.updateOpenedEpic
import flank.scripts.utils.parseToVersion
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import java.time.Instant

private val RAW_GITHUB = "https://raw.githubusercontent.com/$flankGcloudCLIRepository"

fun checkForSDKUpdate(githubToken: String) = runBlocking {
    val lastRun by lazy { runBlocking { getLastSDKUpdateRunDate(githubToken) } }
    val openedUpdates by lazy { runBlocking { checkForOpenedUpdates(githubToken) } }

    println("** Find previously checked commit")
    val oldSha = getCommitsUntilLastCheck(githubToken, lastRun) ?: run {
        println("** Unable to find previous commit")
        return@runBlocking
    }
    println("** Find latest commit")
    val newSha = getCommitsUntilLastCheck(githubToken, Instant.now().toString()) ?: run {
        println("** Unable to find latest commit")
        return@runBlocking
    }

    if (oldSha == newSha) {
        println("** No new commits since the last run")
        return@runBlocking
    }

    with(createContext(oldSha, githubToken, openedUpdates)) {
        println("** New version $newVersion")
        println("** Old version $oldVersion")
        when {
            oldVersion >= newVersion -> println("** No new features")
            openedUpdates != null -> updateOpenedEpic()
            openedUpdates == null -> createEpicIssue()
            else -> return@runBlocking
        }
    }
}

private suspend fun getLastSDKUpdateRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = updateDependenciesWorkflowFilename
)

private suspend fun checkForOpenedUpdates(token: String) = getGitHubIssueList(
    githubToken = token,
    parameters = listOf(
        "creator" to updatesOpenedUser,
        "state" to "open",
        "labels" to "gcloud SDK"
    )
)
    .onError { println(it.message) }
    .getOrElse { emptyList() }
    .firstOrNull()
    .also {
        if (it != null) println("** Issue found: ${it.htmlUrl}")
        else println("** No opened issue")
    }

private fun createContext(sha: String, githubToken: String, openedUpdates: GithubPullRequest?) =
    SDKUpdateContext(
        oldVersion = getVersionBySHA(sha),
        newVersion = getVersionBySHA("master"),
        updatesLazy = suspend {
            val notes = Paths.get(currentPath.toString(), "notes.txt")
            downloadFile("$RAW_GITHUB/master/google-cloud-sdk/RELEASE_NOTES", notes)
            notes.toFile().apply { deleteOnExit() }.readText()
        },
        githubToken = githubToken,
        openedIssue = openedUpdates
    )

private fun getVersionBySHA(sha: String) =
    parseToVersion(Fuel.get("$RAW_GITHUB/$sha/google-cloud-sdk/VERSION").responseString().third.get())
