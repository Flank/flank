package flank.corellium.domain.test.android.device.task

import flank.corellium.api.AndroidTestPlan
import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Authorize
import flank.corellium.domain.TestAndroid.Device
import flank.corellium.domain.TestAndroid.ExecuteTestShard
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.ExecuteTests.Error
import flank.corellium.domain.TestAndroid.ExecuteTests.Result
import flank.corellium.domain.TestAndroid.InstallApks
import flank.corellium.domain.TestAndroid.ParseApkInfo
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.command.formatAmInstrumentCommand
import flank.instrument.log.Instrument
import flank.instrument.log.parseAdbInstrumentLog
import flank.shard.Shard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import java.io.File

/**
 * Executes given test shard on device, and returns the test results.
 *
 * The side effect is console logs from `am instrument` saved inside [TestAndroid.ExecuteTests.ADB_LOG] output subdirectory.
 *
 * The optional parsing errors are sent through [TestAndroid.Context.out].
 */
internal val executeTestShard = ExecuteTestShard from setOf(
    ParseApkInfo,
    Authorize,
    InstallApks,
    ExecuteTests.Results,
) using Device.context {
    val outputDir = File(args.outputDir, ExecuteTests.ADB_LOG).apply { mkdir() }
    val testPlan: AndroidTestPlan.Config = prepareTestPlan()
    coroutineScope {
        ExecuteTests.Plan(testPlan).out()
        api.executeTest(testPlan).map { (id, flow) ->
            async {
                Device.Result(
                    id = id,
                    data = data,
                    value = collectResults(flow, id, outputDir)
                )
            }
        }.awaitAll().also {
            ExecuteTests.Finish(device.id).out()
        }
    }
}

/**
 * Prepare [AndroidTestPlan.Config] for test execution.
 * It is just mapping and formatting the data collected in state.
 */
private fun Device.Context.prepareTestPlan(): AndroidTestPlan.Config =
    AndroidTestPlan.Config(
        mapOf(
            device.id to shard.flatMap { app: Shard.App ->
                app.tests.map { test ->
                    formatAmInstrumentCommand(
                        packageName = packageNames.getValue(test.name),
                        testRunner = testRunners.getValue(test.name),
                        testCases = test.cases.map { case -> "class " + case.name }
                    )
                }
            }
        )
    )

private suspend fun Device.Context.collectResults(
    flow: Flow<String>,
    id: String,
    dir: File,
): List<Instrument> {
    suspend fun Result.send() = also { results.send(it) }
    val file = dir.resolve(id)
    var parsed = file.run { if (exists()) useLines { it.count() } else 0 }
    var read = parsed
    val results = mutableListOf<Instrument>()
    flow.onEach { file.appendText(it + "\n") }
        .buffer(100)
        .flowOn(Dispatchers.IO)
        .onEach { ++read }
        .parseAdbInstrumentLog()
        .onEach { parsed = read }
        .onEach { result -> results += result }
        .onEach { result -> Result(id, result, shard).send().out() }
        .catch { cause -> Error(id, cause, file.path, ++parsed..read).out() }
        .filterIsInstance<Instrument.Result>()
        .take(shard.size)
        .collect()
    return results
}
