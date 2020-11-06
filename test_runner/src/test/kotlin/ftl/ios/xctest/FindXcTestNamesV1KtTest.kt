package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import org.junit.Assume.assumeFalse
import org.junit.Test

class FindXcTestNamesV1KtTest {

    @Test
    fun findTestNames() {
        assumeFalse(FtlConstants.isWindows)

        // when
        val names = findXcTestNamesV1(swiftXcTestRunV1)
            .flatMap { it.value }
            .sorted()

        // then
        assertThat(swiftTestsV1).isEqualTo(names)
    }
}


