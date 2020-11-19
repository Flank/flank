package ftl.ios.xctest.common

import com.dd.plist.NSDictionary
import com.google.common.truth.Truth.assertThat
import ftl.ios.xctest.swiftXcTestRunV1
import ftl.run.exception.FlankGeneralError
import org.junit.Test

class UtilKtTest {

    @Test
    fun parse() {
        val result = parseToNSDictionary(swiftXcTestRunV1)
        assertThat(arrayOf("EarlGreyExampleSwiftTests", "__xctestrun_metadata__")).isEqualTo(result.allKeys())
        val dict = result["EarlGreyExampleSwiftTests"] as NSDictionary
        assertThat(dict.containsKey("OnlyTestIdentifiers")).isFalse()
    }

    @Test(expected = FlankGeneralError::class)
    fun `parse fileNotFound`() {
        parseToNSDictionary("./XctestrunThatDoesNotExist")
    }
}
