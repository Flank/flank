package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.client.google.IncompatibleModelVersion
import ftl.client.google.SupportedDeviceConfig
import ftl.client.google.UnsupportedModelId
import ftl.client.google.UnsupportedVersionId
import org.junit.Test

class SupportedDeviceConfigTest {

    private val projectId = ""

    @Test
    fun supportedDeviceConfig() {
        assertThat(supportedDeviceConfig("ios", "23", projectId))
            .isEqualTo(UnsupportedModelId)
        assertThat(supportedDeviceConfig("NexusLowRes", "twenty-three", projectId))
            .isEqualTo(UnsupportedVersionId)
        assertThat(supportedDeviceConfig("NexusLowRes", "21", projectId))
            .isEqualTo(IncompatibleModelVersion)
        assertThat(supportedDeviceConfig("NexusLowRes", "23", projectId))
            .isEqualTo(SupportedDeviceConfig)
        assertThat(supportedDeviceConfig("brokenModel", "23", projectId))
            .isEqualTo(UnsupportedModelId)
        assertThat(supportedDeviceConfig("does not exist", "23", projectId))
            .isEqualTo(UnsupportedModelId)
    }
}
