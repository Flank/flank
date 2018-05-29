package xctest

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.pow

abstract class TestArtifact {
    internal val fixturesPath = "./src/test/kotlin/xctest/fixtures"

    init {
        val assetLink = remoteAssetLink()
        if (updateRequired(assetLink)) {
            updateFixtures(assetLink)
        }
    }

    private fun getHtml(): Document {
        val githubUrl = "https://github.com/Flank/test_artifacts/releases/latest"
        val maxRetries = 6
        repeat(maxRetries) { attempt ->
            try {
                return Jsoup.connect(githubUrl).get()
            } catch (e: Exception) {
                wait(attempt)
            }
        }
        throw RuntimeException("Unable to connect to '$githubUrl' after $maxRetries attempts!")
    }

    private fun wait(attempt: Int) {
        // use AWS retry_backoff:
        // https://github.com/aws/aws-sdk-ruby/blob/a2e85a4db05f52804a1ec9bc1552732c7bb8a7b8/aws-sdk-core/lib/aws-sdk-core/plugins/retry_errors.rb#L16
        val backoff = 2f.pow(attempt) * 0.3
        sleep(seconds = minOf(5.minutes, backoff.toLong()))
    }

    private fun sleep(seconds: Long) {
        Thread.sleep(seconds.millis)
    }

    private fun remoteAssetLink(): Element {
        val doc = getHtml()
        val downloadLinks = doc.select("li.d-block > a")
        return downloadLinks.find {
            it.attr("href").endsWith(".zip") && it.attr("href").contains("releases/download")
        } ?: throw RuntimeException("Download link not found in html!")
    }

    private fun updateRequired(remoteAssetLink: Element): Boolean {
        val remoteAssetName = remoteAssetLink.attr("href").split("/").last()
        val fixtures = File(fixturesPath)
        if (!fixtures.exists()) return true
        fixtures.listFiles().find {
            it.name == remoteAssetName
        } ?: return true
        return false
    }

    private fun updateFixtures(remoteAssetLink: Element) {
        val fixtures = File(fixturesPath)
        if (fixtures.exists()) fixtures.deleteRecursively()
        fixtures.mkdirs()

        val downloadUrl = "https://github.com${remoteAssetLink.attr("href")}"
        val assetName = remoteAssetLink.attr("href").split("/").last()
        val zipPath = "${fixtures.path}/$assetName"
        val curl = """
            curl
            --connect-timeout 5
            --max-time 300
            --retry 5
            --retry-delay 0
            --retry-max-time 1000
            -L "$downloadUrl"
            -o "$zipPath"
            """.trimIndent().replace("\n", " ")
        Bash.execute(curl)

        val unzip = "unzip \"$zipPath\" -d \"${fixtures.path}\""
        Bash.execute(unzip)
    }
}

private val Int.minutes
    get() = TimeUnit.MINUTES.toSeconds(this.toLong())

private val Long.millis
    get() = TimeUnit.SECONDS.toMillis(this)
