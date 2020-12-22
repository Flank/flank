package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import ftl.ios.xctest.common.parseToNSDictionary
import java.io.File
import org.junit.Assume.assumeFalse
import org.junit.Test

class FindXcTestNamesV2KtTest {

    @Test
    fun findTestNames() {
        assumeFalse(isWindows)

        // given
        val xctestrun = File(swiftXcTestRunV2)

        // when
        val names = findXcTestNamesV2(
            xcTestRoot = xctestrun.parent + "/",
            xcTestNsDictionary = parseToNSDictionary(xctestrun)
        )

        // then
        val sortedNames = names.mapValues { it.value.mapValues { entry -> entry.value.sorted() } }
        assertThat(sortedNames).isEqualTo(swiftTestsV2)
    }
}
