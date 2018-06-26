package ftl.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.StorageClass
import com.google.cloud.storage.StorageOptions
import com.google.common.math.IntMath
import ftl.config.FtlConstants.useMock
import ftl.util.Utils.fatalError
import java.io.File
import java.math.RoundingMode

// testShards - break tests into shards to run the test suite in parallel (converted to numShards in AndroidJUnitRunner)
// https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html
//
// testRuns - how many times to run the tests.

open class YamlConfig(
        val rootGcsBucket: String,

        val disablePerformanceMetrics: Boolean = true,
        val disableVideoRecording: Boolean = false,
        val testTimeoutMinutes: Long = 60,

        testShards: Int = 1,
        testRuns: Int = 1,
        val waitForResults: Boolean = true,
        val testMethods: List<String> = listOf(),
        val limitBreak: Boolean = false,
        val projectId: String = getDefaultProjectId(),
        val devices: List<Device> = listOf(Device()),
        var testShardChunks: Set<Set<String>> = emptySet()) {


    private val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
    private val bucketLabel: Map<String, String> = mapOf(Pair("flank", ""))
    private val storageLocation = "us-central1"

    fun getGcsBucket() : String {
        if (FtlConstants.useMock) return rootGcsBucket

        val bucket = storage.list().values?.find { it.name == rootGcsBucket }
        if (bucket != null) return bucket.name

        return storage.create(BucketInfo.newBuilder(rootGcsBucket)
                .setStorageClass(StorageClass.REGIONAL)
                .setLocation(storageLocation)
                .setLabels(bucketLabel)
                .build()).name
    }

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

    protected fun assertFileExists(file: String, name: String) {
        if (!File(file).exists()) {
            fatalError("'$file' $name doesn't exist")
        }
    }

    protected fun validateTestMethods(validTestMethods: Collection<String>, from: String) {
        val missingMethods = testMethods - validTestMethods

        // todo: update YamConfigTest to use fixture apk with 155 tests, then remove useMock here.
        if (!useMock && missingMethods.isNotEmpty()) fatalError("$from is missing methods: $missingMethods")
        if (validTestMethods.isEmpty()) fatalError("$from has no tests")

        calculateShards(validTestMethods)
    }

    private fun calculateShards(allTestMethods: Collection<String>) {
        var testShardMethods = if (testMethods.isNotEmpty()) {
            testMethods
        } else {
            allTestMethods
        }.sorted()

        if (testShards < 1) testShards = 1

        var chunkSize = IntMath.divide(testShardMethods.size, testShards, RoundingMode.UP)
        // 1 method / 40 shard = 0. chunked(0) throws an exception.
        // default to running all tests in a single chunk if method count is less than shard count.
        if (chunkSize < 1) chunkSize = testShardMethods.size

        testShardChunks = testShardMethods.chunked(chunkSize).map { it.toSet() }.toSet()

        // Ensure we don't create more VMs than requested. VM count per run should be <= testShards
        if (testShardChunks.size > testShards) {
            fatalError("Calculated chunks $testShardChunks is > requested $testShards testShards.")
        }
        if (testShardChunks.isEmpty()) fatalError("Failed to populate test shard chunks")
    }

    companion object {
        private val mapper by lazy { ObjectMapper(YAMLFactory()).registerModule(KotlinModule()) }

        fun <T> load(yamlPath: String, valueType: Class<T>): T {
            val yamlFile = File(yamlPath).canonicalFile
            if (!yamlFile.exists()) {
                fatalError("$yamlFile doesn't exist")
            }

            return mapper.readValue(yamlFile, valueType)
        }

        fun getDefaultProjectId(): String {
            if (useMock) return "mockProjectId"

            return ServiceOptions.getDefaultProjectId() ?: throw RuntimeException(
                    "Project ID not found. Is GOOGLE_CLOUD_PROJECT defined?\n" + " See https://github.com/GoogleCloudPlatform/google-cloud-java#specifying-a-project-id")
        }
    }
}

data class Device(
        val model: String = "NexusLowRes",
        val version: String = "23",
        val locale: String = "en",
        val orientation: String = "portrait")
