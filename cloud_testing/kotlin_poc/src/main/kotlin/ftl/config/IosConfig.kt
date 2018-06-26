package ftl.config

import ftl.ios.IosCatalog
import ftl.util.Utils.fatalError
import ftl.ios.Xctestrun

class IosConfig(
        val xctestrunZip: String = "",
        val xctestrunFile: String = "",

        rootGcsBucket: String,
        disablePerformanceMetrics: Boolean = true,
        disableVideoRecording: Boolean = false,
        testTimeoutMinutes: Long = 60,
        testShards: Int = 1,
        testRuns: Int = 1,
        waitForResults: Boolean = true,
        testMethods: List<String> = listOf(),
        limitBreak: Boolean = false,
        projectId: String = YamlConfig.getDefaultProjectId(),
        devices: List<Device> = listOf(Device()),
        testShardChunks: Set<Set<String>> = emptySet()
): YamlConfig(
        rootGcsBucket,
        disablePerformanceMetrics,
        disableVideoRecording,
        testTimeoutMinutes,
        testShards,
        testRuns,
        waitForResults,
        testMethods,
        limitBreak,
        projectId,
        devices,
        testShardChunks
) {

    init {
        if (!xctestrunZip.startsWith("gs://")) {
            assertFileExists(xctestrunZip, "xctestrunZip")
        }
        assertFileExists(xctestrunFile, "xctestrunFile")

        devices.forEach { device -> assertDeviceSupported(device) }

        val xctestValidTestNames = Xctestrun.findTestNames(xctestrunFile)
        validateTestMethods(xctestValidTestNames, "xctest binary")
    }

    private fun assertDeviceSupported(device: Device) {
        if (!IosCatalog.supported(device.model, device.version)) {
            fatalError("iOS ${device.version} on ${device.model} is not a supported device")
        }
    }

    companion object {
        fun load(yamlPath: String): IosConfig {
            return YamlConfig.load(yamlPath, IosConfig::class.java)
        }
    }

    override fun toString(): String {
        return """IosConfig
            projectId: '$projectId'
            xctestrunZip: '$xctestrunZip',
            xctestrunFile: '$xctestrunFile',
            rootGcsBucket: '$rootGcsBucket',
            disableVideoRecording: $disableVideoRecording,
            testTimeoutMinutes: $testTimeoutMinutes,
            testRuns: $testRuns,
            waitForResults: $waitForResults,
            limitBreak: $limitBreak,
            devices: $devices
            """
    }
}