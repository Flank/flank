package ftl.config

import com.linkedin.dex.parser.DexParser
import ftl.android.*
import ftl.gc.GcStorage
import kotlinx.coroutines.experimental.runBlocking

class AndroidConfig(
        val appApk: String = "",
        val testApk: String = "",
        val autoGoogleLogin: Boolean = true,
        val useOrchestrator: Boolean = true,
        val environmentVariables: Map<String, String> = mapOf(),
        val directoriesToPull: List<String> = listOf(),

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
        devices: List<Device> = listOf(Device("NexusLowRes", "23")),
        testShardChunks: Set<Set<String>> = emptySet()
) : YamlConfig(
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
        if (appApk.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(appApk)
        } else {
            assertFileExists(appApk, "appApk")
        }

        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(testApk)
        } else {
            assertFileExists(testApk, "testApk")
        }

        // Download test APK if necessary so it can be used to validate test methods
        var testLocalApk = testApk
        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            runBlocking {
                testLocalApk = GcStorage.downloadTestApk(this@AndroidConfig)
            }
        }

        devices.forEach { device -> assertDeviceSupported(device) }

        val dexValidTestNames = DexParser.findTestMethods(testLocalApk).map { "class ${it.testName}" }
        validateTestMethods(dexValidTestNames, "Test APK")
    }

    private fun assertDeviceSupported(device: Device) {
        val deviceConfigTest = AndroidCatalog.supportedDeviceConfig(device.model, device.version)
        when (deviceConfigTest) {
            SupportedDeviceConfig -> {
            }
            UnsupportedModelId -> throw RuntimeException("Unsupported model id, '${device.model}'\nSupported model ids: ${AndroidCatalog.androidModelIds}")
            UnsupportedVersionId -> throw RuntimeException("Unsupported version id, '${device.version}'\nSupported Version ids: ${AndroidCatalog.androidVersionIds}")
            is IncompatibleModelVersion -> throw RuntimeException("Incompatible model, '${device.model}', and version, '${device.version}'\nSupported version ids for '${device.model}': $deviceConfigTest")
        }
    }

    companion object {
        fun load(yamlPath: String): AndroidConfig {
            return YamlConfig.load(yamlPath, AndroidConfig::class.java)
        }
    }

    override fun toString(): String {
        return """AndroidConfig
  projectId: '$projectId'
  appApk: '$appApk',
  testApk: '$testApk',
  rootGcsBucket: '$rootGcsBucket',
  autoGoogleLogin: '$autoGoogleLogin',
  useOrchestrator: $useOrchestrator,
  disablePerformanceMetrics: $disablePerformanceMetrics,
  disableVideoRecording: $disableVideoRecording,
  testTimeoutMinutes: $testTimeoutMinutes,
  testShards: $testShards,
  testRuns: $testRuns,
  waitForResults: $waitForResults,
  testMethods: $testMethods,
  limitBreak: $limitBreak,
  devices: $devices,
  environmentVariables: $environmentVariables,
  directoriesToPull: $directoriesToPull
            """
    }
}
