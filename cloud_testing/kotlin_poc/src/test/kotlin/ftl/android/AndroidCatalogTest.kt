package ftl.android

import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class AndroidCatalogTest {

    private val bitrise = System.getenv("BITRISE_IO") != null

    @Test
    fun validateDeviceConfig() {
        assertEquals(UnsupportedModelId, AndroidCatalog.supportedDeviceConfig("ios", "23"))
        assertEquals(UnsupportedVersionId, AndroidCatalog.supportedDeviceConfig("NexusLowRes", "twenty-three"))
        assertEquals(IncompatibleModelVersion, AndroidCatalog.supportedDeviceConfig("NexusLowRes", "21"))
        assertEquals(SupportedDeviceConfig, AndroidCatalog.supportedDeviceConfig("NexusLowRes", "23"))
    }
}
