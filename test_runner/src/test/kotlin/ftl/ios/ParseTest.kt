package ftl.ios

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestArtifact.fixturesPath
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ParseTest {

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
            assertThat(objcTests[index]).isEqualTo(result)
        }

        assertThat(objcTests.size).isEqualTo(results.size)
    }

    @Test(expected = RuntimeException::class)
    fun parseObjcTests_fileNotFound() {
        Parse.parseObjcTests("./BinaryThatDoesNotExist")
    }

    @Test
    fun parseSwiftTests() {
        val results = Parse.parseSwiftTests(swiftBinary).sorted()

        results.forEachIndexed { index, result ->
            assertThat(swiftTests[index]).isEqualTo(result)
        }

        assertThat(swiftTests.size).isEqualTo(results.size)
    }

    @Test(expected = RuntimeException::class)
    fun parseSwiftTests_fileNotFound() {
        Parse.parseSwiftTests("./BinaryThatDoesNotExist")
    }

    @Test(expected = RuntimeException::class)
    fun parseSwiftTests_tmpFolder() {
        Parse.parseSwiftTests("/tmp")
    }
}
