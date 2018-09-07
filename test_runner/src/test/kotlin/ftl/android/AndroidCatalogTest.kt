package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(FlankTestRunner::class)
class AndroidCatalogTest {

    @Test
    fun androidModelIds() {
        assertThat(AndroidCatalog.androidModelIds).isNotEmpty()
    }

    @Test
    fun supportedDeviceConfig() {
        assertThat(AndroidCatalog.supportedDeviceConfig("ios", "23")).isEqualTo(UnsupportedModelId)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "twenty-three")).isEqualTo(UnsupportedVersionId)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "21")).isEqualTo(IncompatibleModelVersion)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "23")).isEqualTo(SupportedDeviceConfig)
        assertThat(AndroidCatalog.supportedDeviceConfig("brokenModel", "23")).isEqualTo(UnsupportedModelId)
        assertThat(AndroidCatalog.supportedDeviceConfig("does not exist", "23")).isEqualTo(UnsupportedModelId)
    }

    @Test
    fun isVirtualDevice() {
        val nexus = AndroidDevice()
        nexus.androidModelId = "NexusLowRes"
        val shamu = AndroidDevice()
        shamu.androidModelId = "shamu"

        assertThat(AndroidCatalog.isVirtualDevice(nexus)).isEqualTo(true)
        assertThat(AndroidCatalog.isVirtualDevice(shamu)).isEqualTo(false)
        assertThat(AndroidCatalog.isVirtualDevice(null)).isEqualTo(false)
    }

    @Test
    fun isVirtualDeviceNullModel() {
        val mockDevice = Mockito.mock(AndroidDevice::class.java)
        Mockito.`when`(mockDevice.androidModelId).thenReturn(null)
        assertThat(AndroidCatalog.isVirtualDevice(mockDevice)).isEqualTo(false)
    }

    @Test
    fun isVirtualDeviceUnknownModel() {
        val mockDevice = Mockito.mock(AndroidDevice::class.java)
        Mockito.`when`(mockDevice.androidModelId).thenReturn("zz")
        assertThat(AndroidCatalog.isVirtualDevice(mockDevice)).isEqualTo(false)
    }

    @Test
    fun isVirtualDeviceBrokenModel() {
        val brokenModel = AndroidDevice()
        brokenModel.androidModelId = "brokenModel"
        assertThat(AndroidCatalog.isVirtualDevice(brokenModel)).isEqualTo(false)
    }
}
