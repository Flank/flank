package ftl.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import ftl.ios.IosCatalog
import ftl.ios.Xctestrun
import ftl.util.Utils.fatalError

class IosConfig(
        @field:JsonProperty("test")
        val xctestrunZip: String = "",
        @field:JsonProperty("xctestrun-file")
        val xctestrunFile: String = "",
        // The following fields are annotated in the base class, GCloudConfig
        rootGcsBucket: String,
        disablePerformanceMetrics: Boolean = true,
        disableVideoRecording: Boolean = false,
        timeout: String = "60m",
        waitForResults: Boolean = true,
        testMethods: List<String> = emptyList(),
        projectId: String = YamlConfig.getDefaultProjectId(),
        devices: List<Device> = listOf(Device("iphone8", "11.2"))
) :
        GCloudConfig(
                rootGcsBucket,
                disablePerformanceMetrics,
                disableVideoRecording,
                timeout,
                waitForResults,
                testMethods,
                projectId,
                devices) {

    init {
        if (xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(xctestrunZip)
        } else {
            assertFileExists(xctestrunZip, "xctestrunZip")
        }
        assertFileExists(xctestrunFile, "xctestrunFile")

        devices.forEach { device -> assertDeviceSupported(device) }

        validTestNames = Xctestrun.findTestNames(xctestrunFile)
        validateTestMethods(validTestNames, "xctest binary")
    }

    private fun assertDeviceSupported(device: Device) {
        if (!IosCatalog.supported(device.model, device.version)) {
            fatalError("iOS ${device.version} on ${device.model} is not a supported device")
        }
    }

    companion object {
        fun load(yamlPath: String): YamlConfig<IosConfig> = YamlConfig.load(yamlPath, object : TypeReference<YamlConfig<IosConfig>>() {})
    }

    override fun toString() = """${super.toString()}

        IosConfig
            xctestrunZip: '$xctestrunZip',
            xctestrunFile: '$xctestrunFile',
            """
}
