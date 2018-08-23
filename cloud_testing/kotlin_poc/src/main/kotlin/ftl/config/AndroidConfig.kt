package ftl.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.linkedin.dex.parser.DexParser
import ftl.android.*
import ftl.gc.GcStorage
import kotlinx.coroutines.experimental.runBlocking

class AndroidConfig(
        @field:JsonProperty("app")
        val appApk: String = "",
        @field:JsonProperty("test")
        val testApk: String = "",
        @field:JsonProperty("auto-google-login")
        val autoGoogleLogin: Boolean = true,
        @field:JsonProperty("use-orchestrator")
        val useOrchestrator: Boolean = true,
        @field:JsonProperty("environment-variables")
        val environmentVariables: Map<String, String> = emptyMap(),
        @field:JsonProperty("directories-to-pull")
        val directoriesToPull: List<String> = emptyList(),
        // The following fields are annotated in the base class, GCloudConfig
        resultsBucket: String,
        performanceMetrics: Boolean = false,
        recordVideo: Boolean = true,
        testTimeout: String = "60m",
        async: Boolean = false,
        testTargets: List<String> = emptyList(),
        projectId: String = YamlConfig.getDefaultProjectId(),
        devices: List<Device> = listOf(Device("NexusLowRes", "23"))
) :
        GCloudConfig(
                resultsBucket,
                performanceMetrics,
                recordVideo,
                testTimeout,
                async,
                testTargets,
                projectId,
                devices) {

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

        validTestNames = DexParser.findTestMethods(testLocalApk).map { "class ${it.testName}" }
        validateTestMethods(validTestNames, "Test APK")
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
        fun load(yamlPath: String): YamlConfig<AndroidConfig> = YamlConfig.load(yamlPath, object : TypeReference<YamlConfig<AndroidConfig>>() {})
    }

    override fun toString() = """${super.toString()}

        AndroidConfig
            app: '$appApk',
            test: '$testApk',
            autoGoogleLogin: '$autoGoogleLogin',
            useOrchestrator: $useOrchestrator,
            environmentVariables: $environmentVariables,
            directoriesToPull: $directoriesToPull
            """
}
