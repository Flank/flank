package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.api.fetchDeviceModelIos
import ftl.environment.ios.toCliTable
import org.junit.Test

class ValidateIosArgsTest {

    private val projectId = ""

    @Test
    fun supportedDevice() {
        assertThat(isDeviceSupported("bogus", "11.2", projectId)).isFalse()
        assertThat(isDeviceSupported("iphone8", "bogus", projectId)).isFalse()
        assertThat(isDeviceSupported("iphone8", "12.0", projectId)).isTrue()
    }

    @Test
    fun `should print available devices as table`() {
        // given
        val expectedHeaders = arrayOf("MODEL_ID", "MODEL_NAME", "RESOLUTION", "OS_VERSION_IDS", "TAGS")
        val expectedSeparatorCount = expectedHeaders.size + 1

        // when
        val devicesTable = fetchDeviceModelIos(projectId).toCliTable()
        val headers = devicesTable.lines()[1]

        // then
        // has all necessary headers
        expectedHeaders.forEach {
            assertThat(headers.contains(it)).isTrue()
        }
        // number of separators match
        assertThat(headers.count { it == '│' }).isEqualTo(expectedSeparatorCount)
    }
}
