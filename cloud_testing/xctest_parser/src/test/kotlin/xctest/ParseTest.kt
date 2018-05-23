package xctest

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

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

    private fun remoteAssetLink(): Element {
        val doc = Jsoup.connect("https://github.com/Flank/test_artifacts/releases/tag/v0.1").get()
        val downloadLinks = doc.select("li.d-block > a")
        return downloadLinks.find {
            it.attr("href").endsWith(".zip") && it.attr("href").contains("releases/download")
        } ?: throw RuntimeException("Download link not found in html!")
    }

    private fun updateRequired(remoteAssetLink: Element): Boolean {
        val remoteAssetName = remoteAssetLink.attr("href").split("/").last()
        val fixtures = File(fixturesPath)
        if (!fixtures.exists()) return true
        val localAssetPath = fixtures.listFiles().find { it.extension == "zip" } ?: return true
        val localAssetMd5 = localAssetPath.readBytes().md5()
        return remoteAssetName != "$localAssetMd5.zip"
    }

    private fun updateFixtures(remoteAssetLink: Element) {
        val fixtures = File(fixturesPath)
        if (fixtures.exists()) fixtures.deleteRecursively()
        fixtures.mkdirs()

        val downloadUrl = "https://github.com${remoteAssetLink.attr("href")}"
        val assetName = remoteAssetLink.attr("href").split("/").last()
        val zipPath = "${fixtures.path}/$assetName"
        val curl = "curl -L $downloadUrl -o $zipPath"
        Parse.execute(curl)

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

fun ByteArray.md5(): String {
    val md5 = MessageDigest.getInstance("MD5")
    md5.update(this)
    return DatatypeConverter.printHexBinary(md5.digest())
}