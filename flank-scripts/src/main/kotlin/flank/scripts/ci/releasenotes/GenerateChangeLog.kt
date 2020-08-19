package flank.scripts.ci.releasenotes

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.map
import flank.scripts.github.getLatestReleaseTag
import flank.scripts.github.getPrDetailsByCommit
import flank.scripts.utils.markdownLink
import flank.scripts.utils.runCommand
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File

fun generateReleaseNotes(githubToken: String) = runBlocking {
    generateReleaseNotes(
        latestReleaseTag = getLatestReleaseTag(githubToken).map { it.tag }.getOrElse(""),
        githubToken = githubToken
    )
}

fun generateReleaseNotes(
    latestReleaseTag: String,
    githubToken: String
) = getCommitsSha(latestReleaseTag).getNewReleaseNotes(githubToken)

internal fun getCommitsSha(fromTag: String): List<String> {
    val outputFile = File.createTempFile("sha", ".log")

    "git log --pretty=%H $fromTag..HEAD".runCommand(fileForOutput = outputFile)

    return outputFile.readLines()
}

private fun List<String>.getNewReleaseNotes(githubToken: String) = runBlocking {
    map { sha -> async { getPrDetailsByCommit(sha, githubToken) } }
        .awaitAll()
        .filterIsInstance<Result.Success<List<GithubPullRequest>>>()
        .mapNotNull { it.value.firstOrNull()?.toReleaseNoteMessage() }
        .fold(mutableMapOf<String, MutableList<String>>()) { grouped, pr ->
            grouped.apply {
                val (type, message) = pr
                appendToType(type, message)
            }
        }
}

private fun MutableMap<String, MutableList<String>>.appendToType(
    type: String,
    message: String
) = apply { (getOrPut(type) { mutableListOf() }).add(message) }

private fun GithubPullRequest.toReleaseNoteMessage() =
    title.mapPrTitleWithType()?.let { (type, title) ->
        type to "- ${markdownLink("#$number", htmlUrl)} $title ${assignees.format()}"
    }

private fun List<GithubUser>.format() = "(${joinToString { (login, url) -> markdownLink(login, url) }})"
