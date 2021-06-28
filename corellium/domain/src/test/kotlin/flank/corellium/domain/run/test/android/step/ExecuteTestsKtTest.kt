package flank.corellium.domain.run.test.android.step

import flank.apk.Apk
import flank.corellium.api.CorelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.ExecuteTests
import flank.corellium.domain.RunTestCorelliumAndroid.ExecuteTests.ADB_LOG
import flank.corellium.domain.invalidLog
import flank.corellium.domain.stubCredentials
import flank.corellium.domain.validLog
import flank.junit.JUnit
import flank.log.Event
import flank.log.Output
import flank.shard.Shard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class ExecuteTestsKtTest : RunTestCorelliumAndroid.Context {

    override lateinit var api: CorelliumApi
    override var out: Output = ::println
    override val apk = Apk.Api()
    override val junit = JUnit.Api()
    override val args = RunTestCorelliumAndroid.Args(
        credentials = stubCredentials,
        apks = emptyList(),
        maxShardsCount = 1,
    )

    private val dir = File(args.outputDir)
    private val instanceId = "1"
    private val state = RunTestCorelliumAndroid.State(
        ids = listOf(instanceId),
        shards = listOf((0..2).map { Shard.App("$it", emptyList()) })
    )

    // simulate additional unneeded input that will be omitted.
    private val additionalInput = (0..1000).map(Int::toString).asFlow().onStart { delay(500) }

    private fun setLog(log: String) {
        api = CorelliumApi(executeTest = {
            listOf(instanceId to flowOf(log.lines().asFlow(), additionalInput).flattenConcat())
        })
    }

    @Before
    fun setUp() {
        dir.mkdirs()
    }

    @After
    fun tearDown() {
        dir.deleteRecursively()
    }

    /**
     * Valid console output should be completely saved in file, parsed and returned as testResult.
     */
    @Test
    fun happyPath() {
        // given
        setLog(validLog)

        // when
        val testResult = runBlocking { executeTests().invoke(state) }.testResult

        // then
        assertEquals(9, testResult.first().size)

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
     * On parsing error, the task will send the [RunTestCorelliumAndroid.ExecuteTests.Error] through [Output].
     */
    @Test
    fun error() {
        // given
        setLog(invalidLog)
        val events = mutableListOf<Event<*>>()
        out = { events += this as Event<*> }

        // when
        val testResult = runBlocking { executeTests()(state).testResult }

        // then
        assertTrue(testResult.first().isNotEmpty()) // Valid lines parsed before error will be returned

        val error = events.mapNotNull { it.value as? ExecuteTests.Error }.first() // Obtain error
        assertEquals(instanceId, error.id) // Error should contain correct instanceId

        val lines = dir.resolve(ADB_LOG).resolve(instanceId).readLines() // Read log saved in file
        assertTrue(lines.size > error.lines.last) // Task can save more output lines than was marked in error which is expected behaviour

        val invalid = lines.indexOfFirst { it.endsWith("INVALID LINE") } + 1 // Obtain invalid line number
        assertTrue(invalid in error.lines) // Error should reference affected lines
    }
}
