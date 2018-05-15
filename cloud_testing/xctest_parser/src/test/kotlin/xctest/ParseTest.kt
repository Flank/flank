package xctest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ParseTest {

    private val objcBinary = File("./src/test/kotlin/xctest/fixtures/objc/EarlGreyExampleTests")
    private val swiftBinary = File("./src/test/kotlin/xctest/fixtures/swift/EarlGreyExampleSwiftTests")

    private val objcTests = mapOf(Pair("EarlGreyExampleTests", listOf(
            "testBasicSelection",
            "testBasicSelectionAndAction",
            "testBasicSelectionAndAssert",
            "testBasicSelectionActionAssert",
            "testSelectionOnMultipleElements",
            "testCollectionMatchers",
            "testWithInRoot",
            "testWithCustomMatcher",
            "testTableCellOutOfScreen",
            "testCatchErrorOnFailure",
            "testCustomAction",
            "testWithCustomAssertion",
            "testWithCustomFailureHandler",
            "testLayout",
            "testWithCondition"
    )))

    private val swiftTests = mapOf(Pair("EarlGreyExampleSwiftTests", listOf(
            "testBasicSelection",
            "testBasicSelectionAndAction",
            "testBasicSelectionAndAssert",
            "testBasicSelectionActionAssert",
            "testSelectionOnMultipleElements",
            "testCollectionMatchers",
            "testWithInRoot",
            "testWithCustomMatcher",
            "testTableCellOutOfScreen",
            "testCatchErrorOnFailure",
            "testCustomAction",
            "testWithCustomAssertion",
            "testWithCustomFailureHandler",
            "testLayout",
            "testWithCondition",
            "testWithGreyAssertions"
    )))

    @Test
    fun parseObjcTests() {
        val results = Parse.parseObjcTests(objcBinary)
        assertTrue(results.isNotEmpty())
        results.forEach { result ->
            val tokens = result.split(' ')
            val klass = tokens.first()
            val method = tokens.last()
            val expectedMethods = objcTests[klass] ?: throw RuntimeException("Expected Class $klass not found!")
            assertEquals(expectedMethods.size, results.size)
            assertTrue(expectedMethods.contains(method))
        }
    }

    @Test(expected=RuntimeException::class)
    fun parseObjcTests_fileNotFound() {
        Parse.parseObjcTests(File("./BinaryThatDoesNotExist"))
    }

    @Test
    fun parseSwiftTests() {
        val results = Parse.parseSwiftTests(swiftBinary)
        assertTrue(results.isNotEmpty())
        results.forEach { result ->
            val tokens = result.split('.')
            val klass = tokens.first()
            val method = tokens.last()
            val expectedMethods = swiftTests[klass] ?: throw RuntimeException("Expected Class $klass not found!")
            assertEquals(expectedMethods.size, results.size)
            assertTrue(expectedMethods.contains(method))
        }
    }

    @Test(expected=RuntimeException::class)
    fun parseSwiftTests_fileNotFound() {
        Parse.parseSwiftTests(File("./BinaryThatDoesNotExist"))
    }

}
