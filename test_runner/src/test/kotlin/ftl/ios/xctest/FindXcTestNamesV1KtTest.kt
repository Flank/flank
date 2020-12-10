package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.ios.xctest.common.parseToNSDictionary
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File

class FindXcTestNamesV1KtTest {

    @Test
    fun findTestNames() {
        assumeFalse(FtlConstants.isWindows)

        // given
        val xctestrun = File(swiftXcTestRunV1)

        // when
        val names = findXcTestNamesV1(
            xcTestRoot = xctestrun.parent + "/",
            xcTestNsDictionary = parseToNSDictionary(xctestrun)
        )
            .flatMap { it.value }
            .sorted()

        // then
        assertThat(swiftTestsV1).isEqualTo(names)
    }
}
