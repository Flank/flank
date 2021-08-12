package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.api.AndroidTestPlan
import flank.corellium.api.Authorization
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.TestAndroid.Args.DefaultOutputDir
import flank.corellium.domain.TestAndroid.Args.DefaultOutputDir.new
import flank.corellium.domain.test.android.device.task.executeTestShard
import flank.corellium.domain.test.android.device.task.installApks
import flank.corellium.domain.test.android.device.task.releaseDevice
import flank.corellium.domain.test.android.task.authorize
import flank.corellium.domain.test.android.task.availableDevices
import flank.corellium.domain.test.android.task.calculateDevicesDuration
import flank.corellium.domain.test.android.task.calculateTestDuration
import flank.corellium.domain.test.android.task.createOutputDir
import flank.corellium.domain.test.android.task.dispatchFailedTests
import flank.corellium.domain.test.android.task.dispatchShards
import flank.corellium.domain.test.android.task.dispatchTests
import flank.corellium.domain.test.android.task.dumpShards
import flank.corellium.domain.test.android.task.executeTestQueue
import flank.corellium.domain.test.android.task.fetchDeviceCostPerSecond
import flank.corellium.domain.test.android.task.finish
import flank.corellium.domain.test.android.task.generateReport
import flank.corellium.domain.test.android.task.initResultsChannel
import flank.corellium.domain.test.android.task.invokeDevices
import flank.corellium.domain.test.android.task.loadPreviousDurations
import flank.corellium.domain.test.android.task.parseApksInfo
import flank.corellium.domain.test.android.task.parseTestCasesFromApks
import flank.corellium.domain.test.android.task.prepareShards
import flank.corellium.domain.test.android.task.processResults
import flank.corellium.domain.test.android.task.sendAnalyticsReport
import flank.exection.parallel.Parallel
import flank.exection.parallel.type
import flank.instrument.log.Instrument
import flank.junit.JUnit
import flank.log.Event
import flank.shard.InstanceShard
import flank.shard.Shard
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import java.io.File
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat

/**
 * Use case for running android tests on corellium virtual devices.
 */
object TestAndroid {

    /**
     * The user arguments for the test execution.
     *
     * @param credentials The user credentials for authorizing connection with API.
     * @param apks List of app apks with related test apks for testing.
     * @param testTargets Test targets to filter.
     * @param maxShardsCount Maximum amount of shards to create. For each shard Flank is invoking dedicated device instance, so do not use values grater than maximum number available instances in the Corellium account.
     * @param obfuscateDumpShards Obfuscate the test names in shards before dumping to file.
     * @param outputDir Set output dir. Default value is [DefaultOutputDir.new]
     * @param gpuAcceleration Enable gpu acceleration for newly created virtual devices.
     * @param scanPreviousDurations Scan the specified amount of JUnitReport.xml files to obtain test cases durations necessary for optimized sharding. The [outputDir] is used for searching JUnit reports.
     */
    data class Args(
        val project: String = "",
        val credentials: Authorization.Credentials = Authorization.Empty,
        val apks: List<Apk.App> = emptyList(),
        val testTargets: List<String> = emptyList(),
        val maxShardsCount: Int = 1,
        val obfuscateDumpShards: Boolean = false,
        val outputDir: String = new,
        val gpuAcceleration: Boolean = true,
        val scanPreviousDurations: Int = 10,
        val flakyTestsAttempts: Int = 0,
        val junitReport: JUnitReportConfig = Report.JUnit.Default,
    ) {

        companion object : Parallel.Type<Args> {
            val Default = Args()
            const val DEFAULT_PROJECT = "Default Project"
            const val AUTH_FILE = "corellium_auth.yml"
        }

        /**
         * Default output directory scheme.
         *
         * @property new A directory name in format: `results/corellium/android/yyyy-MM-dd_HH-mm-ss-SSS`.
         */
        object DefaultOutputDir {
            internal const val ROOT = "results/corellium/android/"
            private val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS")
            val new get() = ROOT + date.format(currentTimeMillis())
        }

        /**
         * Abstraction for app and test apk files.
         *
         * @property path Absolut or relative path to apk file.
         */
        sealed class Apk {
            abstract val path: String

            /**
             * App apk data
             *
             * @property tests List of test apks to run on app apk.
             */
            data class App(
                override val path: String,
                val tests: List<Test>
            ) : Apk()

            /**
             * Test apk data
             */
            data class Test(
                override val path: String
            ) : Apk()
        }

        /**
         * Report configuration file.
         */
        object Report {
            object JUnit {
                enum class Type { Skipped, Passed, Failed, Flaky }

                val Default = mapOf(
                    "failures" to setOf(Type.Failed, Type.Flaky)
                )
            }
        }
    }

    // Context

    /**
     * The context of android test execution on corellium.
     * Is providing access to initial arguments and data collected during the execution process.
     * For convenience the properties are sorted in order equal to its initialization.
     *
     * @property api Corellium API functions.
     * @property apk APK parsing functions.
     * @property junit JUnit parsing functions.
     * @property args User arguments for execution.
     *
     * @property testCases key - path to the test apk, value - list of test method names.
     * @property previousDurations key - test case name, value - calculated previous duration.
     * @property shards each item is representing list of apps to run on another device instance.
     * @property results Channel for providing result from each executed test case.
     * @property dispatch Channel for dispatching test shards to execute.
     * @property devices Channel for providing devices that are available and ready to use.
     * @property ids the ids of corellium device instances.
     * @property rawResults Execution results.
     * @property processResults Results processed according to [Args.junitReport] configuration.
     */
    internal class Context : Parallel.Context() {
        val api by !type<CorelliumApi>()
        val apk by !type<Apk.Api>()
        val junit by !type<JUnit.Api>()
        val args by !Args

        val shards: List<List<Shard.App>> by -PrepareShards
        val packageNames: Map<String, String> by ParseApkInfo { packageNames }
        val testCases: Map<String, List<String>> by -ParseTestCases
        val previousDurations: Map<String, Long> by -LoadPreviousDurations
        val results: Channel<ExecuteTests.Result> by -ExecuteTests.Results
        val dispatch: Channel<Dispatch.Data> by -Dispatch.Shards
        val devices: Channel<Device.Instance> by -AvailableDevices
        val ids: List<String> by -InvokeDevices
        val rawResults: List<Device.Result> by -ExecuteTests
        val processedResults: Map<String, List<Device.Result>> by -ProcessedResults
        val testResult: List<Device.Result> by -ExecuteTests
        val testDuration: Long by -TestDuration
        val devicesDuration: Map<String, Long> by -DevicesDuration
        val costPerSecond: Long by -DeviceCostPerSecond
    }

    internal val context = Parallel.Function(::Context)

    internal const val DEFAULT_TEST_CASE_DURATION = 120L

    // Types

    object ParseTestCases : Parallel.Type<Map<String, List<String>>>
    object LoadPreviousDurations : Parallel.Type<Map<String, Long>> {
        object Searching : Event.Type<Int>
        data class Summary(val unknown: Int, val matching: Int, val required: Int) : Event.Data
    }

    object PrepareShards : Parallel.Type<List<List<Shard.App>>>
    object ParseApkInfo : Parallel.Type<Info>
    object OutputDir : Parallel.Type<Unit>
    object DumpShards : Parallel.Type<Unit>
    object Authorize : Parallel.Type<Unit>
    object AvailableDevices : Parallel.Type<Channel<Device.Instance>>
    object InvokeDevices : Parallel.Type<List<String>> {
        object Status : Event.Type<AndroidInstance.Event>
    }

    object InstallApks : Parallel.Type<List<String>> {
        object Status : Event.Type<AndroidApps.Event>
    }

    object Dispatch {

        object Shards : Parallel.Type<Channel<Data>>
        object Tests : Parallel.Type<List<Device.Result>>
        object Failed : Parallel.Type<Map<InstanceShard, Int>>

        enum class Type { Shard, Rerun }

        data class Data(
            val index: Int,
            val shard: InstanceShard,
            val type: Type,
        ) {
            companion object : Parallel.Type<Data>
        }
    }

    object ExecuteTestShard : Parallel.Type<List<Device.Result>>

    object ExecuteTests : Parallel.Type<List<Device.Result>> {
        const val ADB_LOG = "adb_log"

        object Results : Parallel.Type<Channel<Result>>

        object Plan : Event.Type<AndroidTestPlan.Config>

        data class Dispatch(
            val id: String,
            val data: TestAndroid.Dispatch.Data
        ) : Event.Data

        data class Result(
            val id: String,
            val status: Instrument,
            val shard: InstanceShard,
        ) : Event.Data

        data class Error(
            val id: String,
            val cause: Throwable,
            val logFile: String,
            val lines: IntRange
        ) : Event.Data

        data class Finish(
            val id: String
        ) : Event.Data
    }

    object ReleaseDevice : Parallel.Type<Unit>
    object ProcessedResults : Parallel.Type<Map<String, List<Device.Result>>>


    object TestDuration : Parallel.Type<Long>
    object DevicesDuration : Parallel.Type<Map<String, Long>>
    object DeviceCostPerSecond : Parallel.Type<Long>

    object CleanUp : Parallel.Type<Unit>
    object GenerateReport : Parallel.Type<Unit>
    object AnalyticsReport : Parallel.Type<Unit>
    object CompleteTests : Parallel.Type<Unit>

    // Data

    data class Info(
        val packageNames: Map<String, String> = emptyMap(),
        val testRunners: Map<String, String> = emptyMap(),
    )

    // Common Events

    object Created : Event.Type<File>
    object AlreadyExist : Event.Type<File>

    // Nested

    /**
     * Nested scope that represents shard execution on single device
     */
    object Device {

        /**
         * The context of android test execution on single device.
         *
         * @property api Corellium API functions.
         * @property args User arguments for execution.
         * @property packageNames key - path to the test apk, value - package name.
         * @property testRunners key - path to the test apk, value - fully qualified test runner name.
         * @property shard Tests dispatched to run on device.
         * @property device Device instance specified to execute test shard.
         * @property results Channel for providing result from each executed test case.
         * @property release Channel for releasing instance device after shard execution.
         *
         * @property installedApks List of apk names that was installed on the device during the current execution.
         */
        internal class Context : Parallel.Context() {
            val api by !type<CorelliumApi>()
            val args by !Args
            val packageNames: Map<String, String> by ParseApkInfo { packageNames }
            val testRunners: Map<String, String> by ParseApkInfo { testRunners }
            val data: Dispatch.Data by !Dispatch.Data
            val shard get() = data.shard
            val device: Instance by !Instance
            val results: SendChannel<ExecuteTests.Result> by !ExecuteTests.Results
            val release: SendChannel<Instance> by !AvailableDevices

            val installedApks by -InstallApks
        }

        internal val context = Parallel.Function(::Context)

        object Shard : Parallel.Type<InstanceShard>

        data class Instance(
            val id: String,
            val apks: Set<String> = emptySet(),
            val startTime: Long = currentTimeMillis(),
            val releaseTime: Long = 0,
        ) {
            companion object : Parallel.Type<Instance>
        }

        data class Result(
            val id: String,
            val data: Dispatch.Data,
            val value: List<Instrument>,
            val flakes: Set<String> = emptySet(),
        )

        internal val execute by lazy {
            setOf(
                installApks,
                executeTestShard,
                releaseDevice,
            )
        }
    }

    // Execution tasks

    // Evaluate lazy to avoid strange NullPointerException.
    val execute by lazy {
        // Keep alphabetic order.
        setOf(
            context.validate,
            authorize,
            availableDevices,
            calculateDevicesDuration,
            calculateTestDuration,
            createOutputDir,
            dispatchFailedTests,
            dispatchShards,
            dispatchTests,
            dumpShards,
            executeTestQueue,
            fetchDeviceCostPerSecond,
            finish,
            generateReport,
            initResultsChannel,
            invokeDevices,
            loadPreviousDurations,
            parseApksInfo,
            parseTestCasesFromApks,
            prepareShards,
            processResults,
            sendAnalyticsReport,
        )
    }
}

/**
 * JUnit report configuration.
 *
 * key - suffix that will be added to [JUnit.REPORT_FILE_NAME] for creating custom JUnitReport file name.
 * value - set of required results to include in report.
 */
typealias JUnitReportConfig = Map<String, Set<TestAndroid.Args.Report.JUnit.Type>>
