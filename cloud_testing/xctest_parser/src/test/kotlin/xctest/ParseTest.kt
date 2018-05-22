package xctest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ParseTest {

    private val objcBinary = "./src/test/kotlin/xctest/fixtures/objc/EarlGreyExampleTests"
    private val swiftBinary = "./src/test/kotlin/xctest/fixtures/swift/EarlGreyExampleSwiftTests"

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
        val releaseUrl = "https://github.com/Flank/test_artifacts/releases/download/v0.2/EarlGreyExampleTests.zip"
        val fixtures = File("./src/test/kotlin/xctest/fixtures")
        if (!fixtures.exists()) {
            fixtures.mkdir()

            val zipPath = "${fixtures.path}/release.zip"
            val curl = "curl -L $releaseUrl -o $zipPath"
            Parse.execute(curl)

            val unzip = "unzip $zipPath -d ${fixtures.path}"
            Parse.execute(unzip)
        }
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
