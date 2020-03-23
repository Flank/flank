package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.common.truth.Truth.assertThat
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
    fun supportedDeviceConfig() {
        assertThat(AndroidCatalog.supportedDeviceConfig("ios", "23", projectId)).isEqualTo(UnsupportedModelId)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "twenty-three", projectId)).isEqualTo(UnsupportedVersionId)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "21", projectId)).isEqualTo(IncompatibleModelVersion)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "23", projectId)).isEqualTo(SupportedDeviceConfig)
        assertThat(AndroidCatalog.supportedDeviceConfig("brokenModel", "23", projectId)).isEqualTo(UnsupportedModelId)
        assertThat(AndroidCatalog.supportedDeviceConfig("does not exist", "23", projectId)).isEqualTo(UnsupportedModelId)
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
}
