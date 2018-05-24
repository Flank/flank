package xctest

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.math.pow

class ParseTest {

    private val fixturesPath = "./src/test/kotlin/xctest/fixtures"
    private val objcBinary = "$fixturesPath/objc/EarlGreyExampleTests"
    private val swiftBinary = "$fixturesPath/swift/EarlGreyExampleSwiftTests"

    private val objcTests = listOf(
            "EarlGreyExampleTests/testBasicSelection",
            "EarlGreyExampleTests/testBasicSelectionActionAssert",
            "EarlGreyExampleTests/testBasicSelectionAndAction",
            "EarlGreyExampleTests/testBasicSelectionAndAssert",
            "EarlGreyExampleTests/testCatchErrorOnFailure",
            "EarlGreyExampleTests/testCollectionMatchers",
            "EarlGreyExampleTests/testCustomAction",
            "EarlGreyExampleTests/testLayout",
            "EarlGreyExampleTests/testSelectionOnMultipleElements",
            "EarlGreyExampleTests/testTableCellOutOfScreen",
            "EarlGreyExampleTests/testWithCondition",
            "EarlGreyExampleTests/testWithCustomAssertion",
            "EarlGreyExampleTests/testWithCustomFailureHandler",
            "EarlGreyExampleTests/testWithCustomMatcher",
            "EarlGreyExampleTests/testWithInRoot"
    )

    private val swiftTests = listOf(
            "EarlGreyExampleSwiftTests/testBasicSelection",
            "EarlGreyExampleSwiftTests/testBasicSelectionActionAssert",
            "EarlGreyExampleSwiftTests/testBasicSelectionAndAction",
            "EarlGreyExampleSwiftTests/testBasicSelectionAndAssert",
            "EarlGreyExampleSwiftTests/testCatchErrorOnFailure",
            "EarlGreyExampleSwiftTests/testCollectionMatchers",
            "EarlGreyExampleSwiftTests/testCustomAction",
            "EarlGreyExampleSwiftTests/testLayout",
            "EarlGreyExampleSwiftTests/testSelectionOnMultipleElements",
            "EarlGreyExampleSwiftTests/testTableCellOutOfScreen",
            "EarlGreyExampleSwiftTests/testWithCondition",
            "EarlGreyExampleSwiftTests/testWithCustomAssertion",
            "EarlGreyExampleSwiftTests/testWithCustomFailureHandler",
            "EarlGreyExampleSwiftTests/testWithCustomMatcher",
            "EarlGreyExampleSwiftTests/testWithGreyAssertions",
            "EarlGreyExampleSwiftTests/testWithInRoot"
    )

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

    private fun downloadAsset(cmd: String) {
        val maxRetries = 6
        repeat(maxRetries) { attempt ->
            try {
                Parse.execute(cmd)
                return
            } catch (e: Exception) {
                wait(attempt)
            }
        }
        throw RuntimeException("$cmd failed after $maxRetries attempts!")
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
        val curl = "curl -L $downloadUrl -o $zipPath"
        downloadAsset(curl)

        val unzip = "unzip $zipPath -d ${fixtures.path}"
        Parse.execute(unzip)
    }

    @Test
    fun parseObjcTests() {
        val results = Parse.parseObjcTests(objcBinary).sorted()

        results.forEachIndexed { index, result ->
            assertEquals(objcTests[index], result)
        }

        assertEquals(objcTests.size, results.size)
    }

    @Test(expected=RuntimeException::class)
    fun parseObjcTests_fileNotFound() {
        Parse.parseObjcTests("./BinaryThatDoesNotExist")
    }

    @Test
    fun parseSwiftTests() {
        val results = Parse.parseSwiftTests(swiftBinary).sorted()

        results.forEachIndexed { index, result ->
            assertEquals(swiftTests[index], result)
        }

        assertEquals(swiftTests.size, results.size)
    }

    @Test(expected=RuntimeException::class)
    fun parseSwiftTests_fileNotFound() {
        Parse.parseSwiftTests("./BinaryThatDoesNotExist")
    }

}

private val Int.minutes
    get() = MINUTES.toSeconds(this.toLong())

private val Long.millis
    get() = SECONDS.toMillis(this)
