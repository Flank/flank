package xctest

import org.junit.Assert.assertEquals
import org.junit.Test

class ParseTest : TestArtifact() {

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

    @Test
    fun parseObjcTests() {
        val results = Parse.parseObjcTests(objcBinary).sorted()

        results.forEachIndexed { index, result ->
            assertEquals(objcTests[index], result)
        }

        assertEquals(objcTests.size, results.size)
    }

    @Test(expected = RuntimeException::class)
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

    @Test(expected = RuntimeException::class)
    fun parseSwiftTests_fileNotFound() {
        Parse.parseSwiftTests("./BinaryThatDoesNotExist")
    }

}
