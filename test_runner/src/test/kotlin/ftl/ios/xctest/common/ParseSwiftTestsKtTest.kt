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

        val results = parseSwiftTests(swiftBinary).sorted()
        checkSwiftTests(results)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests fileNotFound`() {
        parseSwiftTests("./BinaryThatDoesNotExist")
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseSwiftTests tmpFolder`() {
        parseSwiftTests("/tmp")
    }

    @Test
    fun `Parse Swift with space in path`() {
        assumeFalse(isWindows)

        val results = parseSwiftTests("$FIXTURES_PATH/sp ace/swift/EarlGreyExampleSwiftTests").sorted()
        checkSwiftTests(results)
    }
}
