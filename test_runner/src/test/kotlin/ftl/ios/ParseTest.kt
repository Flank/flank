package ftl.ios

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants.isWindows
import ftl.test.util.FlankTestRunner
import ftl.run.exception.FlankGeneralError
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ParseTest {

    private val objcBinary = "$FIXTURES_PATH/objc/EarlGreyExampleTests"
    private val swiftBinary = "$FIXTURES_PATH/swift/EarlGreyExampleSwiftTests"

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
        "EarlGreyExampleSwiftTests/testThatThrows",
        "EarlGreyExampleSwiftTests/testWithCondition",
        "EarlGreyExampleSwiftTests/testWithCustomAssertion",
        "EarlGreyExampleSwiftTests/testWithCustomFailureHandler",
        "EarlGreyExampleSwiftTests/testWithCustomMatcher",
        "EarlGreyExampleSwiftTests/testWithGreyAssertions",
        "EarlGreyExampleSwiftTests/testWithInRoot"
    )

    private fun checkObjcTests(actual: List<String>) {
        actual.forEachIndexed { index, result ->
            assertThat(objcTests[index]).isEqualTo(result)
        }

        assertThat(objcTests.size).isEqualTo(actual.size)
    }

    private fun checkSwiftTests(actual: List<String>) {
        actual.forEachIndexed { index, result ->
            assertThat(result).isEqualTo(swiftTests[index])
        }

        assertThat(actual.size).isEqualTo(swiftTests.size)
    }

    @Test
    fun `Parse ObjC and Swift with space in path`() {
        assumeFalse(isWindows)

        var results = Parse.parseObjcTests("$FIXTURES_PATH/sp ace/objc/EarlGreyExampleTests").sorted()
        checkObjcTests(results)

        results = Parse.parseSwiftTests("$FIXTURES_PATH/sp ace/swift/EarlGreyExampleSwiftTests").sorted()
        checkSwiftTests(results)
    }

    @Test
    fun parseObjcTests() {
        assumeFalse(isWindows)

        val results = Parse.parseObjcTests(objcBinary).sorted()
        checkObjcTests(results)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseObjcTests fileNotFound`() {
        Parse.parseObjcTests("./BinaryThatDoesNotExist")
    }

    @Test
    fun parseSwiftTests() {
        assumeFalse(isWindows)

        val results = Parse.parseSwiftTests(swiftBinary).sorted()
        checkSwiftTests(results)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests fileNotFound`() {
        Parse.parseSwiftTests("./BinaryThatDoesNotExist")
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests tmpFolder`() {
        Parse.parseSwiftTests("/tmp")
    }
}
