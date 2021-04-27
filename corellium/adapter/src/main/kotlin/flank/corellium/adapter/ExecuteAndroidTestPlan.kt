package flank.corellium.adapter

import flank.corellium.api.AndroidTestPlan
import flank.corellium.client.console.clear
import flank.corellium.client.console.flowLogs
import flank.corellium.client.console.sendCommand
import flank.corellium.client.console.waitForIdle
import flank.corellium.client.core.connectConsole
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

object ExecuteAndroidTestPlan : AndroidTestPlan.Execute {
    override suspend fun AndroidTestPlan.Config.invoke(): Flow<String> =
        coroutineScope {
            channelFlow {
                instances.map { (instanceId, shards: List<AndroidTestPlan.Shard>) ->
                    println("Getting console $instanceId")
                    launch {
                        corellium.connectConsole(instanceId).run {
                            clear()
                            launch {
                                shards.forEach { shard ->
                                    val command = shard.prepareRunCommand()
                                    println("Sending command: $command")
                                    sendCommand(command)
                                }
                            }
                            launch {
                                flowLogs().collect {
                                    channel.send(it)
                                }
                            }
                            waitForIdle(10_000)
                        }
                    }
                }.joinAll()
            }
        }
}

private fun AndroidTestPlan.Shard.prepareRunCommand(): String {
    val base = "am instrument -r -w "

    val testCases = testCases
        // group test cases by filter type - [class, package]
        .map { it.split(" ") }.groupBy({ it.first() }, { it.last() }).toList()
        // build test cases string
        .joinToString("") { (type, tests) -> "-e $type ${tests.joinToString(",")} " }

    val runner = "$packageName/$testRunner"

    return base + testCases + runner
}
