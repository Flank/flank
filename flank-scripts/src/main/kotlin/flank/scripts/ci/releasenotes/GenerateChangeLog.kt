package flank.scripts.ci.releasenotes

import com.github.kittinunf.result.Result
import flank.scripts.ci.getPrDetailsByCommit
import flank.scripts.utils.markdownLink
import flank.scripts.utils.runCommand
import java.io.File
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

suspend fun generateReleaseNotes(latestReleaseTag: String, githubToken: String) = getCommitsSha(latestReleaseTag).getReleaseNotes(githubToken)

private fun getCommitsSha(fromTag: String): List<String> {
    val outputFile = File.createTempFile("sha", ".log")

    "git log --pretty=%H $fromTag..HEAD".runCommand(fileForOutput = outputFile)

    return outputFile.readLines()
}

private suspend fun List<String>.getReleaseNotes(githubToken: String) = runBlocking {
    map { sha -> async { getPrDetailsByCommit(sha, githubToken) } }
        .awaitAll()
        .filterIsInstance<Result.Success<List<GithubPullRequest>>>()
        .map { it.value }
        .filter { it.isNotEmpty() }
        .mapNotNull { it.first().toReleaseNoteMessage() }
}

fun GithubPullRequest.toReleaseNoteMessage() =
    title.mapPrTitle()?.let { title ->
        "- ${markdownLink(number.toString(), htmlUrl)} $title ${assignees.format()}"
    }

private fun List<GithubUser>.format() = "(${joinToString { (login, url) -> markdownLink(login, url) }})"
