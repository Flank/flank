package flank.corellium.domain.run.test.android.step

import flank.corellium.api.AndroidTestPlan
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.ExecuteTests
import flank.corellium.domain.step
import flank.instrument.command.formatAmInstrumentCommand
import flank.instrument.log.Instrument
import flank.instrument.log.parseAdbInstrumentLog
import flank.shard.Shard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import java.io.File

/**
 * The step is executing tests on previously invoked devices, and returning the test results.
 *
 * The side effect is console logs from `am instrument` saved inside [RunTestCorelliumAndroid.ExecuteTests.ADB_LOG] output subdirectory.
 *
 * The optional parsing errors are sent through [RunTestCorelliumAndroid.Context.out].
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
internal fun RunTestCorelliumAndroid.Context.executeTests() = step(ExecuteTests) { out ->
    val outputDir = File(args.outputDir, ExecuteTests.ADB_LOG).apply { mkdir() }
    val testPlan: AndroidTestPlan.Config = prepareTestPlan()
    val list = coroutineScope {
        api.executeTest(testPlan).map { (id, flow) ->
            async {
                var read = 0
                var parsed = 0
                val file = outputDir.resolve(id)
                val results = mutableListOf<Instrument>()
                flow.onEach { file.appendText(it + "\n") }
                    .buffer(100)
                    .flowOn(Dispatchers.IO)
                    .onEach { ++read }
                    .parseAdbInstrumentLog()
                    .onEach { parsed = read }
                    .onEach { result -> results += result }
                    .onEach { result -> ExecuteTests.Status(id, result).out() }
                    .catch { cause -> ExecuteTests.Error(id, cause, file.path, ++parsed..read).out() }
                    .filterIsInstance<Instrument.Result>()
                    .take(expectedResultsCountFor(id))
                    .collect()
                results
            }
        }.awaitAll()
    }
    copy(testResult = list)
}

/**
 * Prepare [AndroidTestPlan.Config] for test execution.
 * It is just mapping and formatting the data collected in state.
 */
private fun RunTestCorelliumAndroid.State.prepareTestPlan(): AndroidTestPlan.Config =
    AndroidTestPlan.Config(
        shards.mapIndexed { index, shards ->
            ids[index] to shards.flatMap { shard: Shard.App ->
                shard.tests.map { test ->
                    formatAmInstrumentCommand(
                        packageName = packageNames.getValue(test.name),
                        testRunner = testRunners.getValue(test.name),
                        testCases = test.cases.map { case -> "class " + case.name }
                    )
                }
            }
        }.toMap()
    )

private fun RunTestCorelliumAndroid.State.expectedResultsCountFor(id: String): Int =
    shards[ids.indexOf(id)].size
