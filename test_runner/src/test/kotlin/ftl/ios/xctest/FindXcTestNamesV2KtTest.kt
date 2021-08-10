package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import ftl.ios.xctest.common.parseToNSDictionary
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File

class FindXcTestNamesV2KtTest {

    @Test
    fun findTestNames() {
        assumeFalse(isWindows)

        // given
        val xctestrun = File(swiftXcTestRunV2)

        // when
        val names = findXcTestNamesV2(
            xcTestRoot = xctestrun.parent + "/",
            xcTestNsDictionary = parseToNSDictionary(xctestrun),
            globalTestInclusion = true
        )

        // then
        val sortedNames = names.mapValues { it.value.mapValues { entry -> entry.value.sorted() } }
        assertThat(sortedNames).isEqualTo(swiftTestsV2)
    }
}
