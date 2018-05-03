package ftl.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import com.linkedin.dex.parser.DexParser
import ftl.config.FtlConstants.useMock
import ftl.util.Utils.fatalError
import java.io.File

// testShards - break tests into shards to run the test suite in parallel (converted to numShards in AndroidJUnitRunner)
// https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html
//
// testRuns - how many times to run the tests.

class YamlConfig(
        val appApk: String,
        val testApk: String,
        val rootGcsBucket: String,

        val autoGoogleLogin: Boolean = true,
        val useOrchestrator: Boolean = true,
        val disablePerformanceMetrics: Boolean = true,
        val disableVideoRecording: Boolean = false,
        val testTimeoutMinutes: Long = 60,

        testShards: Int = 1,
        testRuns: Int = 1,
        val waitForResults: Boolean = true,
        val testMethods: List<String> = listOf(),
        val limitBreak: Boolean = false,
        val projectId: String = getDefaultProjectId(),
        var testShardChunks: List<List<String>> = emptyList()) {

    private fun assertVmLimit(value: Int): Int {
        if (value > 100 && !limitBreak) {
            fatalError("Shard count exceeds 100. Set limitBreak=true to enable large shards")
        }
        return value
    }

    var testShards: Int = testShards
        set(value) {
            field = assertVmLimit(value)
        }

    var testRuns: Int = testRuns
        set(value) {
            field = assertVmLimit(value)
        }

    init {
        validate()
    }

    private fun validate() {
        if (!File(appApk).exists()) {
            fatalError("'$appApk' appApk doesn't exist")
        }

        if (!File(testApk).exists()) {
            fatalError("'$testApk' testApk doesn't exist")
        }

        val dexValidTestNames = DexParser.findTestMethods(testApk).map { it.testName }
        val missingMethods = mutableListOf<String>()

        testMethods.forEach { testMethod ->
            if (!dexValidTestNames.contains(testMethod)) {
                missingMethods.add(testMethod)
            }
        }

        if (missingMethods.isNotEmpty()) fatalError("Test APK is missing methods: $missingMethods")

        if (testShards >= 1) {
            val testShardMethods = if (testMethods.isEmpty()) { dexValidTestNames } else  { testMethods }
            testShardChunks = testShardMethods.map { "class $it" }.chunked(testShardMethods.size / testShards)
        }
    }

    companion object {
        private val mapper by lazy { ObjectMapper(YAMLFactory()).registerModule(KotlinModule()) }

        fun load(yamlPath: String): YamlConfig {
            val yamlFile = File(yamlPath).canonicalFile
            if (!yamlFile.exists()) {
                fatalError("$yamlFile doesn't exist")
            }

            return mapper.readValue(yamlFile, YamlConfig::class.java)
        }

        private fun getDefaultProjectId(): String {
            if (useMock) return "mockProjectId"

            return ServiceOptions.getDefaultProjectId() ?: throw RuntimeException(
                    "Project ID not found. Is GOOGLE_CLOUD_PROJECT defined?\n" + " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id")
        }
    }

    override fun toString(): String {
        return """YamlConfig
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
  testMethods: $testMethods
  limitBreak: $limitBreak
"""
    }
}
