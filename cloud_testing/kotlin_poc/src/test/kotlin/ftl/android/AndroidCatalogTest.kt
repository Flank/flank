package ftl.android

import com.google.api.services.testing.model.AndroidDevice
import ftl.config.FtlConstants
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class AndroidCatalogTest {

    init {
        FtlConstants.useMock = true
    }

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
