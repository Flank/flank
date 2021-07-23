package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.api.AndroidTestPlan
import flank.corellium.api.Authorization
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.TestAndroid.Args.DefaultOutputDir
import flank.corellium.domain.TestAndroid.Args.DefaultOutputDir.new
import flank.corellium.domain.test.android.task.authorize
import flank.corellium.domain.test.android.task.createOutputDir
import flank.corellium.domain.test.android.task.dumpShards
import flank.corellium.domain.test.android.task.executeTests
import flank.corellium.domain.test.android.task.finish
import flank.corellium.domain.test.android.task.generateReport
import flank.corellium.domain.test.android.task.installApks
import flank.corellium.domain.test.android.task.invokeDevices
import flank.corellium.domain.test.android.task.loadPreviousDurations
import flank.corellium.domain.test.android.task.parseApksInfo
import flank.corellium.domain.test.android.task.parseTestCasesFromApks
import flank.corellium.domain.test.android.task.prepareShards
import flank.exection.parallel.Parallel
import flank.exection.parallel.type
import flank.instrument.log.Instrument
import flank.junit.JUnit
import flank.log.Event
import flank.shard.Shard
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
        val credentials: Authorization.Credentials = Authorization.Empty,
        val apks: List<Apk.App> = emptyList(),
        val testTargets: List<String> = emptyList(),
        val maxShardsCount: Int = 1,
        val obfuscateDumpShards: Boolean = false,
        val outputDir: String = DefaultOutputDir.new,
        val gpuAcceleration: Boolean = true,
        val scanPreviousDurations: Int = 10,
        val flakyTestsAttempts: Int = 0,
    ) {

        companion object : Parallel.Type<Args> {
            val Default = Args()
            const val DEFAULT_PROJECT = "Default Project"
            const val AUTH_FILE = "corellium_auth.yml"
        }

        /**
         * Default output directory scheme.
         *
         * @property new Directory name in format: `results/corellium/android/yyyy-MM-dd_HH-mm-ss-SSS`.
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
    }

    // Context

    /**
     * The context of android test execution on corellium.
     * Is providing access to initial arguments and data collected during the execution process.
     * For convenience the properties are sorted in order equal to its initialization.
     *
     * @property api - Corellium API functions.
     * @property apk - APK parsing functions.
     * @property junit - JUnit parsing functions.
     * @property args - User arguments for execution.
     *
     * @property testCases key - path to the test apk, value - list of test method names.
     * @property previousDurations key - test case name, value - calculated previous duration.
     * @property shards each item is representing list of apps to run on another device instance.
     * @property ids the ids of corellium device instances.
     * @property packageNames key - path to the test apk, value - package name.
     * @property testRunners key - path to the test apk, value - fully qualified test runner name.
     */
    internal class Context : Parallel.Context() {
        val api by !type<CorelliumApi>()
        val apk by !type<Apk.Api>()
        val junit by !type<JUnit.Api>()
        val args by !Args

        val testCases: Map<String, List<String>> by -ParseTestCases
        val previousDurations: Map<String, Long> by -LoadPreviousDurations
        val shards: List<List<Shard.App>> by -PrepareShards
        val ids: List<String> by -InvokeDevices
        val packageNames by ParseApkInfo { packageNames }
        val testRunners by ParseApkInfo { testRunners }
        val testResult: List<List<Instrument>> by -ExecuteTests
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
    object InvokeDevices : Parallel.Type<List<String>> {
        object Status : Event.Type<AndroidInstance.Event>
    }

    object InstallApks : Parallel.Type<Unit> {
        object Status : Event.Type<AndroidApps.Event>
    }

    object ExecuteTests : Parallel.Type<List<List<Instrument>>> {
        const val ADB_LOG = "adb_log"

        object Plan : Event.Type<AndroidTestPlan.Config>

        data class Result(
            val id: String,
            val status: Instrument,
        ) : Event.Data

        data class Error(
            val id: String,
            val cause: Throwable,
            val logFile: String,
            val lines: IntRange
        ) : Event.Data
    }

    object CleanUp : Parallel.Type<Unit>
    object GenerateReport : Parallel.Type<Unit>
    object CompleteTests : Parallel.Type<Unit>

    // Data

    data class Info(
        val packageNames: Map<String, String> = emptyMap(),
        val testRunners: Map<String, String> = emptyMap(),
    )

    // Common Events

    object Created : Event.Type<File>
    object AlreadyExist : Event.Type<File>

    // Execution tasks

    // Evaluate lazy to avoid strange NullPointerException.
    val execute by lazy {
        setOf(
            context.validate,
            authorize,
            createOutputDir,
            dumpShards,
            executeTests,
            finish,
            generateReport,
            installApks,
            invokeDevices,
            loadPreviousDurations,
            parseApksInfo,
            parseTestCasesFromApks,
            prepareShards,
        )
    }
}
