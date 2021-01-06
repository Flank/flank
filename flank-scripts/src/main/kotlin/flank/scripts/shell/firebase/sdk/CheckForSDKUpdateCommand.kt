package flank.scripts.shell.firebase.sdk

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.kittinunf.fuel.Fuel
import flank.common.downloadFile
import flank.scripts.config.flankGcloudCLIRepository
import flank.scripts.shell.utils.currentPath
import flank.scripts.utils.parseToVersion
import kotlinx.coroutines.runBlocking
import java.nio.file.Paths
import java.time.Instant

private val RAW_GITHUB = "https://raw.githubusercontent.com/$flankGcloudCLIRepository"

object CheckForSDKUpdateCommand : CliktCommand(
    name = "checkForSdkUpdate",
    help = "Verifies if there were changes in gcloud sdk that need to be implemented in flank"
) {

    private val githubToken by option(help = "Git Token").required()
    private val zenhubToken by option(help = "Zenhub Token").required()
    private val lastRun by lazy { runBlocking { getLastSDKUpdateRunDate(githubToken) } }
    private val openedUpdates by lazy { runBlocking { checkForOpenedUpdates(githubToken) } }

    override fun run() = runBlocking {
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

        with(createContext(oldSha)) {
            println("** New version $newVersion")
            println("** Old version $oldVersion")
            when {
                oldVersion >= newVersion -> println("** No new features")
                openedUpdates != null -> updateOpenedEpic()
                openedUpdates == null -> createEpicIssue()
            }
        }
    }

    private fun createContext(sha: String) = SDKUpdateContext(
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
}
