package ftl.mock

import ftl.run.exception.FlankGeneralError
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import java.util.zip.ZipFile
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
        val host = "1.1.1.1"

        try {
            val pingResult = InetAddress.getByName(host).isReachable(1000)
            if (pingResult) return true
        } catch (e: Exception) {
            println("TestArtifact couldn't ping $host: $e\n")
        }

        try {
            Socket().use { it.connect(InetSocketAddress(host, 53), 1000) }
            return true
        } catch (e: Exception) {
            println("TestArtifact couldn't connect to $host: $e\n")
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
        throw FlankGeneralError("Unable to connect to '$githubUrl' after $maxRetries attempts!")
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
        val downloadLinks = doc.select("a")
        // /Flank/test_artifacts/releases/download/latest/2552E672A1366D1B3A06452AA7256ABC.zip
        val zipRegex = Regex(".*releases/download/latest/\\H{32}\\.zip$")
        return downloadLinks.find {
            it.attr("href").matches(zipRegex)
        } ?: throw FlankGeneralError("Download link not found in html!")
    }

    private fun updateRequired(remoteAssetLink: Element): Boolean {
        val remoteAssetName = remoteAssetLink.attr("href").split("/").last()
        val fixtures = File(fixturesPath)
        if (!fixtures.exists()) return true
        fixtures.listFiles()?.find {
            it.name == remoteAssetName
        } ?: return true
        return false
    }

    private val httpClient = OkHttpClient()

    private fun download(src: String, dst: String) {
        val request = Request.Builder().url(src).build()
        val response = httpClient.newCall(request).execute()
        val body: ResponseBody = response.body ?: throw FlankGeneralError("null response body downloading $src")

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

        unzipFile(zipPath, fixtures.path)
    }
}

private fun unzipFile(fileName: String, unzipPath: String) {
    println("Unzipping: $fileName to $unzipPath")
    ZipFile(fileName).use { zipFile ->
        zipFile.entries().asSequence().forEach { zipEntry ->
            val outputFile = File(unzipPath, zipEntry.name)

            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
                return@forEach
            }

            outputFile.parentFile.mkdirs()

            zipFile.getInputStream(zipEntry).use { zipEntryInput ->
                outputFile.outputStream().use { output ->
                    zipEntryInput.copyTo(output)
                }
            }
        }
    }
}

private val Int.minutes
    get() = TimeUnit.MINUTES.toSeconds(this.toLong())

private val Long.millis
    get() = TimeUnit.SECONDS.toMillis(this)
