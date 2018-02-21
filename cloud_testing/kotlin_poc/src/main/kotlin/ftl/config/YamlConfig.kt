package ftl.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import com.linkedin.dex.parser.DexParser
import ftl.config.FtlConstants.useMock
import ftl.util.Utils.fatalError
import java.io.File

class YamlConfig(
        val appApk: String,
        val testApk: String,
        val rootGcsBucket: String,

        val useOrchestrator: Boolean = true,
        val disablePerformanceMetrics: Boolean = true,
        val disableVideoRecording: Boolean = false,
        val testTimeoutMinutes: Long = 60,

        shardCount: Int = 1,
        val waitForResults: Boolean = true,
        val testMethods: List<String> = listOf(),
        val limitBreak: Boolean = false,
        val projectId: String = getDefaultProjectId()) {

    var shardCount: Int = shardCount
        set(value) {
            if (value > 100 && !limitBreak) {
                fatalError("Shard count exceeds 100. Set limitBreak=true to enable large shards")
            }
            field = value
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

        val validMethods = DexParser.findTestMethods(testApk).map { it.testName }
        val missingMethods = mutableListOf<String>()

        testMethods.forEach { testMethod ->
            if (!validMethods.contains(testMethod)) {
                missingMethods.add(testMethod)
            }
        }

        if (missingMethods.isNotEmpty()) fatalError("Test APK is missing methods: $missingMethods")
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
            return ServiceOptions.getDefaultProjectId()
        }
    }

    override fun toString(): String {
        return """YamlConfig
  projectId: '$projectId'
  appApk: '$appApk',
  testApk: '$testApk',
  rootGcsBucket: '$rootGcsBucket',
  useOrchestrator: $useOrchestrator,
  disablePerformanceMetrics: $disablePerformanceMetrics,
  disableVideoRecording: $disableVideoRecording,
  testTimeoutMinutes: $testTimeoutMinutes,
  shardCount: $shardCount,
  waitForResults: $waitForResults,
  testMethods: $testMethods
  limitBreak: $limitBreak
"""
    }
}
