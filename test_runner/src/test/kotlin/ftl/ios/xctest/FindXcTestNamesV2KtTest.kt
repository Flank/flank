package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import org.junit.Assume.assumeFalse
import org.junit.Test

class FindXcTestNamesV2KtTest {

    @Test
    fun findTestNames() {
        assumeFalse(FtlConstants.isWindows)

        // when
        val names = findXcTestNamesV2(swiftXcTestRunV2)

        // then
        val sortedNames = names.mapValues { it.value.mapValues { entry -> entry.value.sorted() } }
        assertThat(sortedNames).isEqualTo(swiftTestsV2)
    }
}
