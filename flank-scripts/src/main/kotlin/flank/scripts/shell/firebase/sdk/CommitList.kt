package flank.scripts.shell.firebase.sdk

import flank.scripts.github.getGitHubCommitList
import java.time.Instant
import java.time.format.DateTimeFormatter

suspend fun getCommitsUntilLastCheck(token: String, until: String): String? = getGitHubCommitList(
    githubToken = token,
    parameters = listOf(
        "path" to "google-cloud-sdk/VERSION",
        "per_page" to 10,
        "until" to until
    ),
    repo = "Flank/gcloud_cli"
)
    .get()
    .maxByOrNull { Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it.commit.author.date)) }
    .also {
        if (it != null)
            println(
                """
                |** Commit found: 
                |      SHA: ${it.sha}
                |      timestamp: ${it.commit.author.date}""".trimMargin()
            )
    }?.sha
