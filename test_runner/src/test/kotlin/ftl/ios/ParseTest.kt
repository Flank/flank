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

    private val objcBinary = "$FIXTURES_PATH/ios/earl_grey_example/objc/EarlGreyExampleTests"
    private val swiftBinary = "$FIXTURES_PATH/ios/earl_grey_example/swift/EarlGreyExampleSwiftTests"

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

        var results = ftl.ios.xctest.parseObjcTests("$FIXTURES_PATH/sp ace/objc/EarlGreyExampleTests").sorted()
        checkObjcTests(results)

        results = ftl.ios.xctest.parseSwiftTests("$FIXTURES_PATH/sp ace/swift/EarlGreyExampleSwiftTests").sorted()
        checkSwiftTests(results)
    }

    @Test
    fun parseObjcTests() {
        assumeFalse(isWindows)

        val results = ftl.ios.xctest.parseObjcTests(objcBinary).sorted()
        checkObjcTests(results)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseObjcTests fileNotFound`() {
        ftl.ios.xctest.parseObjcTests("./BinaryThatDoesNotExist")
    }

    @Test
    fun parseSwiftTests() {
        assumeFalse(isWindows)

        val results = ftl.ios.xctest.parseSwiftTests(swiftBinary).sorted()
        checkSwiftTests(results)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests fileNotFound`() {
        ftl.ios.xctest.parseSwiftTests("./BinaryThatDoesNotExist")
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests tmpFolder`() {
        ftl.ios.xctest.parseSwiftTests("/tmp")
    }
}
