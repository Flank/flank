package ftl.android

import com.google.common.truth.Truth.assertThat
import com.google.testing.model.AndroidDevice
import ftl.api.Locale
import ftl.api.Platform
import ftl.api.fetchAndroidOsVersion
import ftl.api.fetchDeviceModelAndroid
import ftl.api.fetchLocales
import ftl.client.google.AndroidCatalog
import ftl.environment.android.toCliTable
import ftl.presentation.cli.firebase.test.locale.toCliTable
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class AndroidCatalogTest {

    private val projectId = ""

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun androidModelIds() {
        assertThat(AndroidCatalog.androidModelIds(projectId)).isNotEmpty()
    }

    @Test
    fun isVirtualDevice() {
        val nexus = AndroidDevice()
        nexus.androidModelId = "NexusLowRes"
        val shamu = AndroidDevice()
        shamu.androidModelId = "shamu"

        assertTrue(AndroidCatalog.isVirtualDevice(nexus, projectId))
        assertFalse(AndroidCatalog.isVirtualDevice(shamu, projectId))
        assertFalse(AndroidCatalog.isVirtualDevice(null, projectId))
    }

    @Test
    fun isVirtualDeviceNullModel() {
        val mockDevice = mockk<AndroidDevice>()
        every { mockDevice.androidModelId } returns null
        assertFalse(AndroidCatalog.isVirtualDevice(mockDevice, projectId))
    }

    @Test
    fun isVirtualDeviceUnknownModel() {
        val mockDevice = mockk<AndroidDevice>()
        every { mockDevice.androidModelId } returns "zz"
        assertFalse(AndroidCatalog.isVirtualDevice(mockDevice, projectId))
    }

    @Test
    fun isVirtualDeviceBrokenModel() {
        val brokenModel = AndroidDevice()
        brokenModel.androidModelId = "brokenModel"
        assertFalse(AndroidCatalog.isVirtualDevice(brokenModel, projectId))
    }

    @Test
    fun `should print available devices as table`() {
        // given
        val expectedHeaders =
            arrayOf("MODEL_ID", "MAKE", "MODEL_NAME", "FORM", "RESOLUTION", "OS_VERSION_IDS", "TAGS")
        val expectedSeparatorCount = expectedHeaders.size + 1

        // when
        val devicesTable = fetchDeviceModelAndroid(projectId).list.toCliTable()
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
    fun `should print available software versions as table`() {
        // given
        val expectedHeaders =
            arrayOf("OS_VERSION_ID", "VERSION", "CODE_NAME", "API_LEVEL", "RELEASE_DATE", "TAGS")
        val expectedSeparatorCount = expectedHeaders.size + 1

        // when
        val devicesTable = fetchAndroidOsVersion(projectId).toCliTable()
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
        val devicesTable = fetchLocales(Locale.Identity(projectId, Platform.ANDROID)).toCliTable()
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
