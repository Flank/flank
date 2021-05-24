package flank.scripts.ops.common

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.map
import flank.scripts.data.github.getLatestReleaseTag
import flank.scripts.data.github.getPrDetailsByCommit
import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.data.github.objects.GithubUser
import flank.scripts.utils.markdownLink
import flank.scripts.utils.runCommand
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File

fun generateReleaseNotes(githubToken: String) = runBlocking {
    generateReleaseNotes(
        latestReleaseTag = getLatestReleaseTag(githubToken).map { it.tag }.getOrElse { "" },
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

internal fun String.mapPrTitleWithType() = when {
    startsWith("feat") -> "Features" to skipConventionalCommitPrefix().replaceFirstChar { it.uppercase() }
    startsWith("fix") -> "Bug Fixes" to skipConventionalCommitPrefix().replaceFirstChar { it.uppercase() }
    startsWith("docs") -> "Documentation" to skipConventionalCommitPrefix().replaceFirstChar { it.uppercase() }
    startsWith("refactor") -> "Refactor" to skipConventionalCommitPrefix().replaceFirstChar { it.uppercase() }
    startsWith("ci") -> "CI Changes" to skipConventionalCommitPrefix().replaceFirstChar { it.uppercase() }
    startsWith("test") -> "Tests update" to skipConventionalCommitPrefix().replaceFirstChar { it.uppercase() }
    startsWith("perf") -> "Performance upgrade" to skipConventionalCommitPrefix().replaceFirstChar { it.uppercase() }
    else -> null // we do not accept other prefix to have update in release notes
}

private fun String.skipConventionalCommitPrefix() = substring(indexOf(':') + 2)

private fun List<GithubUser>.format() = "(${joinToString { (login, url) -> markdownLink(login, url) }})"
