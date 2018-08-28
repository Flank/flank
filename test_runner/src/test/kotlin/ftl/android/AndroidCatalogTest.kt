package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class AndroidCatalogTest {

    @Test
    fun validateDeviceConfig() {
        assertThat(AndroidCatalog.supportedDeviceConfig("ios", "23")).isEqualTo(UnsupportedModelId)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "twenty-three")).isEqualTo(UnsupportedVersionId)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "21")).isEqualTo(IncompatibleModelVersion)
        assertThat(AndroidCatalog.supportedDeviceConfig("NexusLowRes", "23")).isEqualTo(SupportedDeviceConfig)
    }

    @Test
    fun validateIsVirtualDevice() {
        val nexus = AndroidDevice()
        nexus.androidModelId = "NexusLowRes"
        val shamu = AndroidDevice()
        shamu.androidModelId = "shamu"

        assertThat(AndroidCatalog.isVirtualDevice(nexus)).isEqualTo(true)
        assertThat(AndroidCatalog.isVirtualDevice(shamu)).isEqualTo(false)
        assertThat(AndroidCatalog.isVirtualDevice(null)).isEqualTo(false)
    }
}
