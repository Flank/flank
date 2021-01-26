package flank.scripts.ops.shell.firebase.sdk

import com.github.kittinunf.fuel.Fuel
import flank.common.config.flankGcloudCLIRepository
import flank.common.currentPath
import flank.common.downloadFile
import flank.scripts.github.objects.GithubPullRequest
import flank.scripts.utils.parseToVersion
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import java.time.Instant

private val RAW_GITHUB = "https://raw.githubusercontent.com/$flankGcloudCLIRepository"

fun checkForSDKUpdate(githubToken: String, zenhubToken: String) = runBlocking {
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

    with(createContext(oldSha, githubToken, zenhubToken, openedUpdates)) {
        println("** New version $newVersion")
        println("** Old version $oldVersion")
        when {
            oldVersion >= newVersion -> println("** No new features")
            openedUpdates != null -> updateOpenedEpic()
            openedUpdates == null -> createEpicIssue()
        }
    }
}

private fun createContext(sha: String, githubToken: String, zenhubToken: String, openedUpdates: GithubPullRequest?) =
    SDKUpdateContext(
        oldVersion = getVersionBySHA(sha),
        newVersion = getVersionBySHA("master"),
        updatesLazy = suspend {
            val notes = Paths.get(currentPath.toString(), "notes.txt")
            downloadFile("$RAW_GITHUB/master/google-cloud-sdk/RELEASE_NOTES", notes)
            notes.toFile().apply { deleteOnExit() }.readText()
        },
        githubToken = githubToken,
        zenhubToken = zenhubToken,
        openedIssue = openedUpdates
    )

private fun getVersionBySHA(sha: String) =
    parseToVersion(Fuel.get("$RAW_GITHUB/$sha/google-cloud-sdk/VERSION").responseString().third.get())
