package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(FlankTestRunner::class)
class AndroidCatalogTest {

    private val projectId = ""

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

        assertThat(AndroidCatalog.isVirtualDevice(nexus, projectId)).isEqualTo(true)
        assertThat(AndroidCatalog.isVirtualDevice(shamu, projectId)).isEqualTo(false)
        assertThat(AndroidCatalog.isVirtualDevice(null, projectId)).isEqualTo(false)
    }

    @Test
    fun isVirtualDeviceNullModel() {
        val mockDevice = Mockito.mock(AndroidDevice::class.java)
        Mockito.`when`(mockDevice.androidModelId).thenReturn(null)
        assertThat(AndroidCatalog.isVirtualDevice(mockDevice, projectId)).isEqualTo(false)
    }

    @Test
    fun isVirtualDeviceUnknownModel() {
        val mockDevice = Mockito.mock(AndroidDevice::class.java)
        Mockito.`when`(mockDevice.androidModelId).thenReturn("zz")
        assertThat(AndroidCatalog.isVirtualDevice(mockDevice, projectId)).isEqualTo(false)
    }

    @Test
    fun isVirtualDeviceBrokenModel() {
        val brokenModel = AndroidDevice()
        brokenModel.androidModelId = "brokenModel"
        assertThat(AndroidCatalog.isVirtualDevice(brokenModel, projectId)).isEqualTo(false)
    }
}
