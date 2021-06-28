package flank.corellium.adapter

import flank.corellium.api.AndroidTestPlan
import flank.corellium.client.console.Console
import flank.corellium.client.console.close
import flank.corellium.client.console.flowLogs
import flank.corellium.client.console.sendCommand
import flank.corellium.client.core.Corellium
import flank.corellium.client.core.connectConsole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ExecuteAndroidTestPlanKtTest {

    /**
     * Connection to [Console] should be closed once subscription detach.
     */
    @Test
    fun closeConsole() {
        // given
        mockkStatic(
            "flank.corellium.client.console.ApiKt",
            "flank.corellium.client.core.ApiKt",
        )

        val instanceId = "1"
        val command = "command"
        val emitted = mutableListOf<Char>()
        val range = ('a'..'z')
        val linesFlow: Flow<String> = channelFlow { range.forEach { send("$it"); emitted += it; delay(10) } }
        val config = AndroidTestPlan.Config(mapOf(instanceId to listOf(command)))

        val console: Console = mockk(relaxed = true) {
            val context = Job()
            every { flowLogs() } returns linesFlow
            every { coroutineContext } returns context
        }

        val corellium: Corellium = mockk(relaxed = true) {
            coEvery { connectConsole(instanceId) } returns console
        }

        val flow: Flow<String> = executeAndroidTestPlan { corellium }
            .invoke(config).first().second

        // when
        runBlocking { flow.first { it == "c" } } // Detach the subscription after "c" element

        // then
        coVerify(exactly = 1) { console.sendCommand(command) } // Verify sendCommand called.
        coVerify(exactly = 1) { console.close() } // Verify console close.
        Assert.assertNotEquals(range.last, emitted.last()) // Check the emission not reach last element.
    }
}
