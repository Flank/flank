package ftl.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.StorageClass
import com.google.cloud.storage.StorageOptions
import com.google.common.math.IntMath
import com.linkedin.dex.parser.DexParser
import ftl.config.FtlConstants.useMock
import ftl.gc.GcStorage
import ftl.ios.IosCatalog
import ftl.ios.Xctestrun
import ftl.util.Utils.fatalError
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.math.RoundingMode
import java.net.URI

// testShards - break tests into shards to run the test suite in parallel (converted to numShards in AndroidJUnitRunner)
// https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html
//
// testRuns - how many times to run the tests.

class YamlConfig(
        val appApk: String = "",
        val testApk: String = "",
        val xctestrunZip: String = "",
        val xctestrunFile: String = "",
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
        val devices: List<Device> = listOf(Device()),
        var testShardChunks: Set<Set<String>> = emptySet(),
        val environmentVariables: Map<String, String> = mapOf(),
        val directoriesToPull: List<String> = listOf()) {

    private val storage = StorageOptions.newBuilder().setProjectId(projectId).build().service
    private val bucketLabel: Map<String, String> = mapOf(Pair("flank", ""))
    private val storageLocation = "us-central1"

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

    fun getGcsBucket(): String {
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

    private fun assertFileExists(file: String, name: String) {
        if (!File(file).exists()) {
            fatalError("'$file' $name doesn't exist")
        }
    }

    private fun assertIosDeviceSupported(device: Device) {
        if (!IosCatalog.supported(device.model, device.version)) {
            fatalError("iOS ${device.version} on ${device.model} is not a supported device")
        }
    }

    private fun assertGcsFileExists(uri: String) {
        val gcsURI = URI.create(uri)
        val bucket = gcsURI.authority
        val path = gcsURI.path.drop(1) // Drop leading slash

        val blob = GcStorage.storage.get(bucket, path)

        if (blob == null) {
            fatalError("The file at '$uri' does not exist")
        }
    }

    private fun validateAndroid() {
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
        var testLocalApk = this@YamlConfig.testApk
        if (this@YamlConfig.testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            runBlocking {
                testLocalApk = GcStorage.downloadTestApk(this@YamlConfig)
            }
        }

        val dexValidTestNames = DexParser.findTestMethods(testLocalApk).map { it.testName }
        val missingMethods = mutableListOf<String>()

        testMethods.forEach { testMethod ->
            if (!dexValidTestNames.contains(testMethod)) {
                missingMethods.add(testMethod)
            }
        }

        // todo: update YamConfigTest to use fixture apk with 155 tests, then remove useMock here.
        if (!useMock && missingMethods.isNotEmpty()) fatalError("Test APK is missing methods: $missingMethods")
        if (dexValidTestNames.isEmpty()) fatalError("Test APK has no tests")

        calculateShards(dexValidTestNames)
    }

    private fun validateIos() {
        if (!xctestrunZip.startsWith("gs://")) {
            assertFileExists(xctestrunZip, "xctestrunZip")
        }
        assertFileExists(xctestrunFile, "xctestrunFile")

        devices.forEach { device -> assertIosDeviceSupported(device) }

        calculateShards(Xctestrun.findTestNames(xctestrunFile))
    }

    fun iOS(): Boolean = xctestrunZip.isNotBlank()

    private fun android(): Boolean = !iOS()

    private fun validate() {
        if (iOS()) {
            validateIos()
        } else {
            validateAndroid()
        }
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

        // FTL requires 'class ' prefix on Android.
        if (android()) {
            testShardMethods = testShardMethods.map { "class $it" }
        }

        testShardChunks = testShardMethods.chunked(chunkSize).map { it.toSet() }.toSet()

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
        // Only print out platform relevant information
        if (iOS()) {
            return """YamlConfig
                projectId: '$projectId'
                xctestrunZip: '$xctestrunZip',
                xctestrunFile: '$xctestrunFile',
                rootGcsBucket: '$rootGcsBucket',
                disablePerformanceMetrics: $disablePerformanceMetrics,
                disableVideoRecording: $disableVideoRecording,
                testTimeoutMinutes: $testTimeoutMinutes,
                testRuns: $testRuns,
                waitForResults: $waitForResults,
                limitBreak: $limitBreak,
                devices: $devices
                """
        } else {
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
                testMethods: $testMethods,
                limitBreak: $limitBreak,
                devices: $devices,
                environmentVariables: $environmentVariables,
                directoriesToPull: $directoriesToPull
                """
        }
    }
}

data class Device(
        val model: String = "NexusLowRes",
        val version: String = "23",
        val locale: String = "en",
        val orientation: String = "portrait")
