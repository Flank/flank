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
    config.instances.map { (instanceId, shards: List<AndroidTestPlan.Shard>) ->
        channelFlow {
            println("Getting console $instanceId")
            corellium.connectConsole(instanceId).apply {
                clear()
                launch {
                    shards.map { it.prepareRunCommand() }
                        .onEach { println("Sending command: $it") }
                        .forEach { sendCommand(it) }
                }
                launch { flowLogs().collect(channel::send) }
                waitForIdle(10_000)
            }
        }
    }
}

private fun AndroidTestPlan.Shard.prepareRunCommand(): String {
    val testCases = testCases // example: listOf("class foo.Bar#baz")
        .map { it.split(" ") } // example: listOf(listOf("class", "foo.Bar#baz"))
        .groupBy({ it.first() }, { it.last() }) // example: first => "class", last => "foo.Bar#baz"
        .toList().joinToString("") { (type, tests: List<String>) ->
            "-e $type ${tests.joinToString(",")} " // example: "-e class foo.Bar#baz"
        } // example: "-e class foo.Bar#baz1,foo.Bar#baz2 -e package foo.test "

    val runner = "$packageName/$testRunner"

    // example: "am instrument -r -w -e class foo.Bar#baz foo.test/androidx.test.runner.AndroidJUnitRunner"
    return AM_INSTRUMENT + testCases + runner
}

private const val AM_INSTRUMENT = "am instrument -r -w "
