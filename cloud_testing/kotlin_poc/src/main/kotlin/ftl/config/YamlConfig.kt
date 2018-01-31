package ftl.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

class YamlConfig(
        val appApk: String,
        val testApk: String,
        val rootGcsBucket: String,

        val useOrchestrator: Boolean = true,
        val disablePerformanceMetrics: Boolean = true,
        val disableVideoRecording: Boolean = false,
        val testTimeoutMinutes: Long = 60,

        val shardCount: Int = 1,
        val waitForResults: Boolean = true,
        val testMethods: List<String> = listOf()) {

    companion object {
        private val mapper by lazy { ObjectMapper(YAMLFactory()).registerModule(KotlinModule()) }

        fun load(yamlPath: String): YamlConfig {
            val yamlFile = File(yamlPath).canonicalFile
            if (!yamlFile.exists()) {
                throw RuntimeException("$yamlFile doesn't exist")
            }

            return mapper.readValue(yamlFile, YamlConfig::class.java)
        }

        fun load(yamlFile: File): YamlConfig {
            return mapper.readValue(yamlFile, YamlConfig::class.java)
        }
    }

    override fun toString(): String {
        return """YamlConfig
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
"""
    }
}
