package flank.corellium.adapter

import flank.corellium.api.AndroidTestPlan
import flank.corellium.client.console.clear
import flank.corellium.client.console.close
import flank.corellium.client.console.flowLogs
import flank.corellium.client.console.sendCommand
import flank.corellium.client.console.waitForIdle
import flank.corellium.client.core.connectConsole
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun executeAndroidTestPlan(
    corellium: GetCorellium
) = AndroidTestPlan.Execute { config ->
    config.instances.map { (instanceId, commands: List<String>) ->
        instanceId to channelFlow<String> {
            corellium().connectConsole(instanceId).apply {
                invokeOnClose { launch { close() } }
                clear()
                launch { commands.forEach { string -> sendCommand(string) } }
                launch { flowLogs().collect(channel::trySendBlocking) }
                waitForIdle(10_000)
            }
        }
    }
}
