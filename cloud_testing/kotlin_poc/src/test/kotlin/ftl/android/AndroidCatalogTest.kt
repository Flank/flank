package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import junit.framework.Assert.assertEquals
import org.junit.Test

class AndroidCatalogTest {

    @Test
    fun validateDeviceConfig() {
        assertEquals(UnsupportedModelId, AndroidCatalog.supportedDeviceConfig("ios", "23"))
        assertEquals(UnsupportedVersionId, AndroidCatalog.supportedDeviceConfig("NexusLowRes", "twenty-three"))
        assertEquals(IncompatibleModelVersion, AndroidCatalog.supportedDeviceConfig("NexusLowRes", "21"))
        assertEquals(SupportedDeviceConfig, AndroidCatalog.supportedDeviceConfig("NexusLowRes", "23"))
    }

    @Test
    fun validateIsVirtualDevice() {
        val nexus = AndroidDevice()
        nexus.androidModelId = "NexusLowRes"
        val shamu = AndroidDevice()
        shamu.androidModelId = "shamu"

        assertEquals(true, AndroidCatalog.isVirtualDevice(nexus))
        assertEquals(false, AndroidCatalog.isVirtualDevice(shamu))
        assertEquals(false, AndroidCatalog.isVirtualDevice(null))
    }
}
