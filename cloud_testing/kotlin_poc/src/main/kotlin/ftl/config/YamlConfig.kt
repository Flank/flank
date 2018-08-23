package ftl.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import ftl.config.FtlConstants.useMock
import ftl.util.Utils.fatalError
import java.io.File

// testShards - break tests into shards to run the test suite in parallel (converted to numShards in AndroidJUnitRunner)
// https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html
//
// testRuns - how many times to run the tests.

open class YamlConfig<MOBILE_CONFIG : GCloudConfig>(
        @field:JsonProperty("gcloud")
        val gCloudConfig: MOBILE_CONFIG,
        @field:JsonProperty("flank")
        val flankConfig: FlankConfig) {

    init {
        flankConfig.calculateShards(gCloudConfig)
    }


    fun getGcsBucket(): String = gCloudConfig.getGcsBucket()

    override fun toString() = """$gCloudConfig

        $flankConfig
    """

    companion object {
        private val mapper by lazy { ObjectMapper(YAMLFactory()).registerModule(KotlinModule()) }

        fun <MOBILE_CONFIG : GCloudConfig> load(yamlPath: String, typeRef: TypeReference<YamlConfig<MOBILE_CONFIG>>): YamlConfig<MOBILE_CONFIG> {
            val yamlFile = File(yamlPath).canonicalFile
            if (!yamlFile.exists()) {
                fatalError("$yamlFile doesn't exist")
            }

            return mapper.readValue(yamlFile, typeRef)
        }

        fun getDefaultProjectId(): String {
            if (useMock) return "mockProjectId"

            return ServiceOptions.getDefaultProjectId() ?: throw RuntimeException(
                    "Project ID not found. Is GOOGLE_CLOUD_PROJECT defined?\n" + " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id")
        }
    }
}

data class Device(
        val model: String,
        val version: String,
        val locale: String = "en",
        val orientation: String = "portrait")
