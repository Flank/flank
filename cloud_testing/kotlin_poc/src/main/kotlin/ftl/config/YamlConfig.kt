package ftl.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import com.google.common.math.IntMath
import com.linkedin.dex.parser.DexParser
import ftl.config.FtlConstants.useMock
import ftl.util.Utils.fatalError
import java.io.File
import java.math.RoundingMode

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
        if (dexValidTestNames.isEmpty()) fatalError("Test APK has no tests")

        calculateShards(dexValidTestNames)
    }

    private fun calculateShards(dexValidTestNames: List<String>) {
        val testShardMethods = if (testMethods.isNotEmpty()) {
            testMethods
        } else {
            dexValidTestNames
        }

        if (testShards < 1) testShards = 1

        var chunkSize = IntMath.divide(testShardMethods.size, testShards, RoundingMode.UP)
        // 1 method / 40 shard = 0. chunked(0) throws an exception.
        // default to running all tests in a single chunk if method count is less than shard count.
        if (chunkSize < 1) chunkSize = testShardMethods.size
        testShardChunks = testShardMethods.map { "class $it" }.chunked(chunkSize)

        // Ensure we don't create more VMs than requested. VM count per run should be <= testShards
        if (testShardChunks.size > testShards) {
            fatalError("Calculated chunks $testShardChunks is > requested $testShards testShards.")
        }
        if (testShardChunks.isEmpty()) fatalError("Failed to populate test shard chunks")
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
