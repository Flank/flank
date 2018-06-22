package ftl.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.cloud.ServiceOptions
import com.google.common.math.IntMath
import com.linkedin.dex.parser.DexParser
import ftl.config.FtlConstants.useMock
import ftl.ios.IosCatalog
import ftl.util.Utils.fatalError
import xctest.Xctestrun
import java.io.File
import java.math.RoundingMode
import java.util.*

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

    private fun validateAndroid() {
        assertFileExists(appApk, "appApk")
        assertFileExists(testApk, "testApk")

        val dexValidTestNames = DexParser.findTestMethods(testApk).map { it.testName }
        val missingMethods = mutableListOf<String>()

        testMethods.forEach { testMethod ->
            if (!dexValidTestNames.contains(testMethod)) {
                missingMethods.add(testMethod)
            }
        }

        // todo: update YamConfigTest to use fixture apk with 155 tests, then remove useMock here.
        if (!useMock && missingMethods.isNotEmpty()) fatalError("Test APK is missing methods: $missingMethods")
        if (dexValidTestNames.isEmpty()) fatalError("Test APK has no tests")

        var testList1 = ArrayList<Pair<String, Int>>()
        var testList2 = ArrayList<String>()

        // Randomly assign tests duration
        for(test in testMethods) {
            val time = (10..120).random()
            testList1.add(Pair(test, time))
            testList2.add(test + "duration=" + time)
        }

        calculateShardsByTime(testList1)
        calculateShards(testList2) // Current chunking approach
    }

    private fun validateIos() {
        if (!xctestrunZip.startsWith("gs://")) {
            assertFileExists(xctestrunZip, "xctestrunZip")
        }
        assertFileExists(xctestrunFile, "xctestrunFile")

        devices.forEach { device -> assertIosDeviceSupported(device) }

        calculateShards(Xctestrun.findTestNames(xctestrunFile))
    }

    fun iOS(): Boolean {
        return xctestrunZip.isNotBlank()
    }

    private fun android(): Boolean {
        return !iOS()
    }

    private fun validate() {
        if (iOS()) {
            validateIos()
        } else {
            validateAndroid()
        }
    }

    fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) +  start

    private fun calculateShardsByTime(testList: ArrayList<Pair<String, Int>>) {
//        if (testMethods.isNotEmpty()) {
//            testMethods.sorted()
//        }

        // use the all methods list
        testList.sortByDescending { it.second }

        if (testShards < 1) testShards = 1

        val shardList = ArrayList<ArrayList<String>>()
        var shardCount = 0
        var startingDuration = testList.first().second

        while (testList.size > 0) {
            if(shardCount == testShards) {
                // we have "filled" all shards, reset starting duration/position
                startingDuration = testList.first().second
                System.out.println("Starting duration " + startingDuration)
                shardCount = 0
            }
            if(shardList.size > shardCount) {
                shardList[shardCount].addAll(fillShard(testList, startingDuration))
            } else {
                shardList.add(fillShard(testList, startingDuration))
            }
            shardCount++
        }

        testShardChunks = shardList.map { it.toSet() }.toSet()


        System.out.println("WITH TIME BASED SORTING")
        var totalTime = 0
        var timelist = ArrayList<Int>()
        for (shard in shardList) {
            var shardTotal = 0
            for (test in shard) {
                shardTotal += test.substringAfter("duration=").toInt()
            }
            timelist.add(shardTotal)
            totalTime += shardTotal
        }

        System.out.println("Slowest shard = " + timelist.max())
        System.out.println("Total Time = " + totalTime)

        // Ensure we don't create more VMs than requested. VM count per run should be <= testShards
        if (testShardChunks.size > testShards) {
            fatalError("Calculated chunks $testShardChunks is > requested $testShards testShards.")
        }
        if (testShardChunks.isEmpty()) fatalError("Failed to populate test shard chunks")
    }

    private fun fillShard(testList: ArrayList<Pair<String, Int>>, startingDuration: Int) : ArrayList<String> {
        var shardDuration = startingDuration
        val testIterator = testList.iterator()
        val shard = ArrayList<String>()

        while(testIterator.hasNext()) {
            val test = testIterator.next()

            if (shardDuration - test.second > 0) {
                shard.add(test.first + " duration=" + test.second.toString())
                shardDuration -= test.second
                testIterator.remove()
            } else if (shardDuration == startingDuration && test.second >= shardDuration) {
                shard.add(test.first + " duration=" + test.second.toString())
                testIterator.remove()
                return shard
            }
        }
        return shard
    }

    private fun calculateShards(allTestMethods: Collection<String>) {
//        var testShardMethods = if (testMethods.isNotEmpty()) {
//            testMethods
//        } else {
//            allTestMethods
//        }.sorted()
        var testShardMethods = allTestMethods.sorted()

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

        System.out.println("WITHOUT TIME SORTING")

        var timelist = ArrayList<Int>()
        var totalTime = 0
        for (shard in testShardChunks) {
            var shardTotal = 0
            for (test in shard) {
                shardTotal += test.substringAfter("duration=").toInt()
            }
            totalTime += shardTotal
            timelist.add(shardTotal)
        }

        System.out.println("Slowest shard = " + timelist.max())
        System.out.println("Total Time = " + totalTime)

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
                directoriesToPull: $directoriesToPull,
                testShardChunks: $testShardChunks
                """
        }
    }
}

data class Device(
        val model: String = "NexusLowRes",
        val version: String = "23",
        val locale: String = "en",
        val orientation: String = "portrait")
