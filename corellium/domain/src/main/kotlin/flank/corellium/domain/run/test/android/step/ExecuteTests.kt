package flank.corellium.domain.run.test.android.step

import flank.corellium.api.AndroidTestPlan
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.log.Instrument
import flank.corellium.log.parseAdbInstrumentLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList

/**
 * The step is executing tests on previously invoked devices.
 *
 * require:
 * * [RunTestCorelliumAndroid.Context.authorize]
 * * [RunTestCorelliumAndroid.Context.prepareShards]
 * * [RunTestCorelliumAndroid.Context.invokeDevices]
 * * [RunTestCorelliumAndroid.Context.installApks]
 * * [RunTestCorelliumAndroid.Context.parseApksInfo]
 *
 * updates:
 * * [RunTestCorelliumAndroid.State.shards]
 */
internal fun RunTestCorelliumAndroid.Context.executeTests() = RunTestCorelliumAndroid.step {
    println("* Executing tests")
    val testPlan: AndroidTestPlan.Config = prepareTestPlan()
    val list = coroutineScope {
        api.executeTest(testPlan).mapIndexed { index, flow ->
            async {
                flow
                    .flowOn(Dispatchers.IO)
                    .dropWhile { !it.startsWith("INSTRUMENTATION_STATUS") }
                    .parseAdbInstrumentLog()
                    .onEach { status ->
                        if (status is Instrument.Status) {
                            val line = "$index: " + status.details.run { "$className#$testName" } + " - " + status.code
                            println(line)
                        }
                    }
                    .toList()
            }
        }.awaitAll()
    }
    copy(testResult = list)
}

/**
 * Prepare [AndroidTestPlan.Config] for test execution.
 * It's a simple data mapping, no API calls or logical operations.
 */
private fun RunTestCorelliumAndroid.State.prepareTestPlan(): AndroidTestPlan.Config =
    AndroidTestPlan.Config(
        shards.mapIndexed { index, shards ->
            ids[index] to shards.flatMap { shard ->
                shard.tests.map { test ->
                    AndroidTestPlan.Shard(
                        packageName = packageNames.getValue(test.name),
                        testRunner = testRunners.getValue(test.name),
                        testCases = test.cases.map { case ->
                            "class " + case.name
                        }
                    )
                }
            }
        }.toMap()
    )
