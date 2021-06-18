package flank.corellium.domain

import flank.apk.Apk
import flank.corellium.api.Authorization
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid.Context
import flank.corellium.domain.RunTestCorelliumAndroid.State
import flank.corellium.domain.run.test.android.step.authorize
import flank.corellium.domain.run.test.android.step.cleanUpInstances
import flank.corellium.domain.run.test.android.step.createOutputDir
import flank.corellium.domain.run.test.android.step.dumpShards
import flank.corellium.domain.run.test.android.step.executeTests
import flank.corellium.domain.run.test.android.step.finish
import flank.corellium.domain.run.test.android.step.generateReport
import flank.corellium.domain.run.test.android.step.installApks
import flank.corellium.domain.run.test.android.step.invokeDevices
import flank.corellium.domain.run.test.android.step.loadPreviousDurations
import flank.corellium.domain.run.test.android.step.parseApksInfo
import flank.corellium.domain.run.test.android.step.parseTestCasesFromApks
import flank.corellium.domain.run.test.android.step.prepareShards
import flank.corellium.domain.util.CreateTransformation
import flank.corellium.domain.util.execute
import flank.instrument.log.Instrument
import flank.junit.JUnit
import flank.shard.Shard
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat

/**
 * Use case for running android tests on corellium virtual devices.
 */
object RunTestCorelliumAndroid {

    /**
     * The context of android test execution on corellium.
     * Is providing all necessary data and operations for [Context.invoke].
     */
    interface Context {
        val api: CorelliumApi
        val apk: Apk.Api
        val junit: JUnit.Api
        val args: Args
    }

    /**
     * The user arguments for the test execution.
     *
     * @param credentials The user credentials for authorizing connection with API.
     * @param apks List of app apks with related test apks for testing.
     * @param maxShardsCount Maximum amount of shards to create. For each shard Flank is invoking dedicated device instance, so do not use values grater than maximum number available instances in the Corellium account.
     * @param obfuscateDumpShards Obfuscate the test names in shards before dumping to file.
     * @param outputDir Set output dir. Default value is [DefaultOutputDir.new]
     * @param gpuAcceleration Enable gpu acceleration for newly created virtual devices.
     * @param scanPreviousDurations Scan the specified amount of JUnitReport.xml files to obtain test cases durations necessary for optimized sharding. The [outputDir] is used for searching JUnit reports.
     */
    data class Args(
        val credentials: Authorization.Credentials,
        val apks: List<Apk.App>,
        val maxShardsCount: Int,
        val obfuscateDumpShards: Boolean = false,
        val outputDir: String = DefaultOutputDir.new,
        val gpuAcceleration: Boolean = true,
        val scanPreviousDurations: Int = 10,
    ) {
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

    /**
     * The State structure is holding all necessary data collected during the execution process.
     * For convenience the properties are sorted in order equal to its initialization.
     *
     * @param testCases key - path to the test apk, value - list of test method names.
     * @param previousDurations key - test case name, value - calculated previous duration.
     * @param shards each item is representing list of apps to run on another device instance.
     * @param ids the ids of corellium device instances.
     * @param packageNames key - path to the test apk, value - package name.
     * @param testRunners key - path to the test apk, value - fully qualified test runner name.
     */
    internal data class State(
        val testCases: Map<String, List<String>> = emptyMap(),
        val previousDurations: Map<String, Long> = defaultPreviousDurations,
        val shards: List<List<Shard.App>> = emptyList(),
        val ids: List<String> = emptyList(),
        val packageNames: Map<String, String> = emptyMap(),
        val testRunners: Map<String, String> = emptyMap(),
        val testResult: List<List<Instrument>> = emptyList(),
    )

    private const val DEFAULT_TEST_CASE_DURATION = 120L

    private val defaultPreviousDurations = emptyMap<String, Long>().withDefault { DEFAULT_TEST_CASE_DURATION }

    /**
     * The reference to the step factory.
     * Invoke it to generate new execution step.
     */
    internal val step = CreateTransformation<State>()
}

operator fun Context.invoke(): Unit = runBlocking {
    State() execute flowOf(
        authorize(),
        parseTestCasesFromApks(),
        loadPreviousDurations(),
        prepareShards(),
        createOutputDir(),
        dumpShards(),
        invokeDevices(),
        installApks(),
        parseApksInfo(),
        executeTests(),
        cleanUpInstances(),
        generateReport(),
        finish(),
    )
}
