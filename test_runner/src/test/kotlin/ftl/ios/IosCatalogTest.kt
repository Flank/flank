package ftl.ios

import com.google.common.truth.Truth.assertThat
import ftl.api.Locale.Identity
import ftl.api.Platform
import ftl.api.fetchLocales
import ftl.client.google.IosCatalog
import ftl.presentation.cli.firebase.test.locale.toCliTable
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosCatalogTest {

    private val projectId = ""

    @Test
    fun supportedXcode() {
        assertThat(IosCatalog.supportedXcode("14.2", projectId)).isTrue()
        assertThat(IosCatalog.supportedXcode("0.1", projectId)).isFalse()
    }

    @Test
    fun `should print available software versions as table`() {
        // given
        val expectedHeaders = arrayOf("OS_VERSION_ID", "MAJOR_VERSION", "MINOR_VERSION", "TAGS", "SUPPORTED_XCODE_VERSION_IDS")
        val expectedSeparatorCount = expectedHeaders.size + 1

        // when
        val devicesTable = IosCatalog.softwareVersionsAsTable(projectId)
        val headers = devicesTable.lines()[1]

        // then
        // has all necessary headers
        expectedHeaders.forEach {
            assertThat(headers.contains(it)).isTrue()
        }
        // number of separators match
        assertThat(headers.count { it == '│' }).isEqualTo(expectedSeparatorCount)
    }

    @Test
    fun `should print available locales as table`() {
        // given
        val expectedHeaders =
            arrayOf("LOCALE", "NAME", "REGION", "TAGS")
        val expectedSeparatorCount = expectedHeaders.size + 1

        // when
        val devicesTable = fetchLocales(Identity(projectId, Platform.IOS)).toCliTable()
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
