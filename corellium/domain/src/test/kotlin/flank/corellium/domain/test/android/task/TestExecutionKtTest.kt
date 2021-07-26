package flank.corellium.domain.test.android.task

import flank.corellium.api.AndroidInstance
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Args
import flank.corellium.domain.TestAndroid.Authorize
import flank.corellium.domain.TestAndroid.TestExecution
import flank.corellium.domain.TestAndroid.TestExecution.ADB_LOG
import flank.corellium.domain.TestAndroid.ParseApkInfo
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.corellium.domain.invalidLog
import flank.corellium.domain.stubCredentials
import flank.corellium.domain.validLog
import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState
import flank.exection.parallel.invoke
import flank.exection.parallel.select
import flank.exection.parallel.type
import flank.exection.parallel.validate
import flank.exection.parallel.verify
import flank.log.Event
import flank.log.Output
import flank.shard.Shard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class TestExecutionKtTest {

    private val dir = File(Args.DefaultOutputDir.new)
    private val instanceId = "1"

    private val initial: ParallelState = mapOf(
        Args to Args(
            credentials = stubCredentials,
            apks = emptyList(),
            maxShardsCount = 1,
            outputDir = dir.path
        ),
        PrepareShards to listOf((0..2).map { Shard.App("$it", emptyList()) }),
        ParseApkInfo to TestAndroid.Info(),
        Authorize to Unit,
    )

    private val dependencies = setOf(
        initResultsChannel,
        availableDevices,
        invokeDevices,
        dispatchShards,
        dispatchTests,
        dispatchFailedTests,
    )

    private val execute = dependencies + testExecution

    // simulate additional unneeded input that will be omitted.
    private val additionalInput = (0..1000).map(Int::toString).asFlow().onStart { delay(500) }
    private fun corelliumApi(log: String) = CorelliumApi(
        invokeAndroidDevices = { config ->
            (0 until config.amount).asFlow().map { id ->
                AndroidInstance.Event.Ready("$id")
            }
        },
        installAndroidApps = { emptyFlow() },
        executeTest = { listOf(instanceId to flowOf(log.lines().asFlow(), additionalInput).flattenConcat()) }
    )

    @Before
    fun setUp() {
        dir.mkdirs()
    }

    @After
    fun tearDown() {
        dir.deleteRecursively()
    }

    @Test
    fun validate() {
        execute.validate(initial)
    }

    /**
     * Valid console output should be completely saved in file, parsed and returned as testResult.
     */
    @Test
    fun happyPath() {
        // given
        val args = initial + mapOf(
            type<CorelliumApi>() to corelliumApi(validLog)
        )

        // when
        val testResult = runBlocking { execute(args).last() }.verify()

        // then
        assertEquals(9, testResult.let(context).shardResults.first().value.size)

        // Right after reading the required results count from validLog the stream is closing.
        // Saved log is same as validLog without unneeded additionalInput.
        assertEquals(
            validLog,
            dir.resolve(ADB_LOG)
                .resolve(instanceId)
                .readText()
                .trimEnd()
        )
    }

    /**
     * On parsing error, the task will send the [TestAndroid.TestExecution.Error] through [Output].
     */
    @Test
    fun error() {
        // given
        val events = mutableListOf<Event<*>>()
        val out: Output = { events += this as Event<*> }
        val args = initial + mapOf(
            type<CorelliumApi>() to corelliumApi(invalidLog),
            Parallel.Logger to out
        )
        // when
        val testResult = runBlocking { execute(args).last().select(TestExecution) }

        // then
        assertTrue(testResult.let(context).shardResults.first().value.isNotEmpty()) // Valid lines parsed before error will be returned

        val error = events.mapNotNull { it.value as? TestExecution.Error }.first() // Obtain error
        assertEquals(instanceId, error.id) // Error should contain correct instanceId

        val lines = dir.resolve(ADB_LOG).resolve(instanceId).readLines() // Read log saved in file
        assertTrue(lines.size > error.lines.last) // Task can save more output lines than was marked in error which is expected behaviour

        val invalid = lines.indexOfFirst { it.endsWith("INVALID LINE") } + 1 // Obtain invalid line number
        assertTrue(invalid in error.lines) // Error should reference affected lines
    }
}
