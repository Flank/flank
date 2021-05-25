package flank.corellium.adapter

import flank.corellium.api.AndroidTestPlan
import flank.corellium.client.console.clear
import flank.corellium.client.console.flowLogs
import flank.corellium.client.console.sendCommand
import flank.corellium.client.console.waitForIdle
import flank.corellium.client.core.connectConsole
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

val executeAndroidTestPlan = AndroidTestPlan.Execute { config ->
    config.instances.map { (instanceId, commands: List<String>) ->
        channelFlow<String> {
            println("Getting console $instanceId")
            corellium.connectConsole(instanceId).apply {
                clear()
                launch {
                    commands.forEach { string ->
                        println("Sending command: $string")
                        sendCommand(string)
                    }
                }
                launch { flowLogs().collect(channel::send) }
                waitForIdle(10_000)
            }
        }
    }
}
