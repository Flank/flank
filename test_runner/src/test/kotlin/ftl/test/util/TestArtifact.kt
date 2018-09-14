package ftl.test.util

import ftl.util.Bash
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object TestArtifact {
    const val fixturesPath = "./src/test/kotlin/ftl/fixtures/tmp"

    val checkFixtures by lazy {
        if (online()) {
            val assetLink = remoteAssetLink()
            if (updateRequired(assetLink)) {
                updateFixtures(assetLink)
            }
        }
    }

    private fun online(): Boolean {
        try {
            Socket().use { it.connect(InetSocketAddress("1.1.1.1", 53), 1000) }
            return true
        } catch (_: Exception) {
        }
        return false
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

    private val httpClient = OkHttpClient()

    private fun download(src: String, dst: String) {
        val request = Request.Builder().url(src).build()
        val response = httpClient.newCall(request).execute()
        val body: ResponseBody = response.body() ?: throw RuntimeException("null response body downloading $src")

        Files.write(Paths.get(dst), body.bytes())
    }

    private fun updateFixtures(remoteAssetLink: Element) {
        val fixtures = File(fixturesPath)
        if (fixtures.exists()) fixtures.deleteRecursively()
        fixtures.mkdirs()

        val downloadUrl = "https://github.com${remoteAssetLink.attr("href")}"
        val assetName = remoteAssetLink.attr("href").split("/").last()
        val zipPath = "${fixtures.path}/$assetName"
        download(downloadUrl, zipPath)

        val unzip = "unzip \"$zipPath\" -d \"${fixtures.path}\""
        Bash.execute(unzip)
        File(zipPath).copyTo(File("${fixtures.path}/EarlGreyExample.zip"))
    }
}

private val Int.minutes
    get() = TimeUnit.MINUTES.toSeconds(this.toLong())

private val Long.millis
    get() = TimeUnit.SECONDS.toMillis(this)
