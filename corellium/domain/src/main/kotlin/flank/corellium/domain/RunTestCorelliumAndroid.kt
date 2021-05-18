package flank.corellium.domain

import flank.corellium.api.Authorization
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid.Context
import flank.corellium.domain.RunTestCorelliumAndroid.State
import flank.corellium.domain.util.CreateTransformation
import flank.corellium.domain.util.execute
import flank.corellium.log.Instrument
import flank.corellium.shard.Shard
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat

object RunTestCorelliumAndroid {

    /**
     * The context of android test execution on corellium.
     * Is providing all necessary data and operations for [Context.invoke].
     */
    interface Context {
        val api: CorelliumApi
        val args: Args
    }

    /**
     * The user configuration for invocation.
     */
    data class Args(
        val credentials: Authorization.Credentials,
        val apks: List<Apk.App>,
        val maxShardsCount: Int,
        val obfuscateDumpShards: Boolean = false,
        val outputDir: String = DefaultOutputDir.new,
    ) {
        object DefaultOutputDir {
            private const val PATH = "results/corellium/android/"
            private val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS")
            val new get() = PATH + date.format(currentTimeMillis())
        }

        /**
         * Abstraction for app and test apk files.
         * @property path Absolut or relative path to apk file.
         */
        sealed class Apk {
            abstract val path: String

            data class App(
                override val path: String,
                val tests: List<Test>
            ) : Apk()

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
     * @param shards each item is representing list of apps to run on another device instance.
     * @param ids the ids of corellium device instances.
     * @param packageNames key - path to the test apk, value - package name.
     * @param testRunners key - path to the test apk, value - fully qualified test runner name.
     */
    internal data class State(
        val testCases: Map<String, List<String>> = emptyMap(),
        val shards: List<List<Shard.App>> = emptyList(),
        val ids: List<String> = emptyList(),
        val packageNames: Map<String, String> = emptyMap(),
        val testRunners: Map<String, String> = emptyMap(),
        val testResult: List<List<Instrument>> = emptyList(),
    )

    /**
     * The reference to the step factory.
     * Invoke it to generate new execution step.
     */
    internal val step = CreateTransformation<State>()
}

operator fun Context.invoke(): Unit = runBlocking {
    State() execute flowOf(
        // TODO steps will be added in next PR
    )
}