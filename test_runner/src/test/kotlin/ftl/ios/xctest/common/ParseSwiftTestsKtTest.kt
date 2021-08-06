package ftl.ios.xctest.common

import flank.common.isWindows
import ftl.ios.xctest.FIXTURES_PATH
import ftl.ios.xctest.checkSwiftTests
import ftl.ios.xctest.swiftBinary
import ftl.run.exception.FlankGeneralError
import org.junit.Assume.assumeFalse
import org.junit.Test

class ParseSwiftTestsKtTest {

    @Test
    fun parseSwiftTests() {
        assumeFalse(isWindows)

        val results = parseSwiftTests(swiftBinary, true).sorted()
        checkSwiftTests(results)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests fileNotFound`() {
        assumeFalse(isWindows)

        parseSwiftTests("./BinaryThatDoesNotExist", true)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests tmpFolder`() {
        assumeFalse(isWindows)

        parseSwiftTests("/tmp", true)
    }

    @Test
    fun `Parse Swift with space in path`() {
        assumeFalse(isWindows)

        val results = parseSwiftTests("$FIXTURES_PATH/sp ace/swift/EarlGreyExampleSwiftTests", true).sorted()
        checkSwiftTests(results)
    }
}
