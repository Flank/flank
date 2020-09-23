package flank.scripts.testartifacts.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.concurrent.TimeUnit
import kotlin.math.pow

internal fun fetchRemoteAssetLink(release: String = RELEASE_LATEST): Element {
    val downloadLinks = getReleaseHtml(release).select("a")
    // /Flank/test_artifacts/releases/download/latest/2552E672A1366D1B3A06452AA7256ABC.zip
    val zipRegex = assetLinkRegex(release)
    return downloadLinks.find { element ->
        element.attr("href").matches(zipRegex)
    } ?: throw Exception("Download link not found in html!")
}

private fun assetLinkRegex(release: String) = Regex(".*releases/download/$release/\\H{32}\\.zip$")

private fun getReleaseHtml(name: String): Document {
    val githubUrl = "https://github.com/Flank/test_artifacts/releases/$name"
    val maxRetries = 6
    repeat(maxRetries) { attempt ->
        try {
            return Jsoup.connect(githubUrl).get()
        } catch (e: Exception) {
            wait(attempt)
        }
    }
    throw Exception("Unable to connect to '$githubUrl' after $maxRetries attempts!")
}

private fun wait(attempt: Int) {
    // use AWS retry_backoff:
    // https://github.com/aws/aws-sdk-ruby/blob/a2e85a4db05f52804a1ec9bc1552732c7bb8a7b8/aws-sdk-core/lib/aws-sdk-core/plugins/retry_errors.rb#L16
    val backoff = 2f.pow(attempt) * 0.3
    Thread.sleep(minOf(5.minutes, backoff.toLong()).millis)
}

private val Int.minutes
    get() = TimeUnit.MINUTES.toSeconds(this.toLong())

private val Long.millis
    get() = TimeUnit.SECONDS.toMillis(this)
