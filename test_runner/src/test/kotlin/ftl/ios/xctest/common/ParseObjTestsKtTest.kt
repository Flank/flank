package ftl.ios.xctest.common

import flank.common.isWindows
import ftl.ios.xctest.FIXTURES_PATH
import ftl.ios.xctest.checkObjcTests
import ftl.ios.xctest.objcBinary
import ftl.run.exception.FlankGeneralError
import org.junit.Assume.assumeFalse
import org.junit.Test

class ParseObjTestsKtTest {

    @Test
    fun parseObjcTests() {
        assumeFalse(isWindows)

        val results = parseObjcTests(objcBinary).sorted()
        checkObjcTests(results)
    }

    @Test(expected = FlankGeneralError::class)
    fun `parseObjcTests fileNotFound`() {
        assumeFalse(isWindows)

        parseObjcTests("./BinaryThatDoesNotExist")
    }

    @Test
    fun `Parse ObjC with space in path`() {
        assumeFalse(isWindows)

        val results = parseObjcTests("$FIXTURES_PATH/sp ace/objc/EarlGreyExampleTests").sorted()
        checkObjcTests(results)
    }
}
