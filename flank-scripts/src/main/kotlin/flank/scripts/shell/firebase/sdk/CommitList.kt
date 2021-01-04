package flank.scripts.shell.firebase.sdk

import flank.scripts.config.flankGcloudCLIRepo
import flank.scripts.github.getGitHubCommitList
import flank.scripts.github.objects.GitHubCommit
import java.time.Instant
import java.time.format.DateTimeFormatter

suspend fun getCommitsUntilLastCheck(token: String, until: String): String? = getGitHubCommitList(
    githubToken = token,
    parameters = listOf(
        "path" to "google-cloud-sdk/VERSION",
        "per_page" to 10,
        "until" to until
    ),
    repo = flankGcloudCLIRepo
)
    .get()
    .maxByOrNull { Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it.commit.author.date)) }
    .also(::logCommitFound)
    ?.sha

private fun logCommitFound(commit: GitHubCommit?) {
    if (commit != null)
        println(
            """
            |** Commit found: 
            |      SHA: ${commit.sha}
            |      timestamp: ${commit.commit.author.date}""".trimMargin()
        )
}
