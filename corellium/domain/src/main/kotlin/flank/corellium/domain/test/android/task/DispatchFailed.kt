package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.shard.InstanceShard
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.atomic.AtomicInteger

/**
 * Collects each [ExecuteTests.Results] and checks its status.
 * Each failed is dispatched again at most as many times as specified in flakyTestsAttempts argument.
 * Rerunning failed tests help detect flakiness.
 */
internal val dispatchFailedTests = Dispatch.Failed from setOf(
    PrepareShards,
    Dispatch.Shards,
    ExecuteTests.Results,
) using context {
    val counter = { AtomicInteger(0) }
    val runs = mutableMapOf<InstanceShard, AtomicInteger>()
    var index = 0
    results.consumeEach { result ->
        if (result.status is Instrument.Status) {
            if (result.status.code in errorCodes) {
                val shard = result.shard
                    .reduceTo(result.status.details.fullTestName)
                val attempt = runs
                    .getOrPut(shard, counter)
                    .getAndIncrement()
                if (attempt < args.flakyTestsAttempts)
                    dispatch.send(
                        Dispatch.Data(
                            index = index++,
                            shard = shard,
                            type = Dispatch.Type.Rerun,
                        )
                    )
            }
        }
    }
    runs.mapValues { (_, value) -> value.get() }
}

/**
 * Set of [Instrument] error codes.
 */
private val errorCodes = setOf(
    Instrument.Code.FAILED,
    Instrument.Code.EXCEPTION,
)

/**
 * Create new [InstanceShard] that contains only one test with basing on given [name].
 *
 * If given [name] refers parameterized test method, algorithm will return [InstanceShard] with test case class instead of test case method.
 */
private fun InstanceShard.reduceTo(
    name: String,
): InstanceShard =
    mapNotNull { app ->
        app.tests.mapNotNull { test ->
            test.cases.mapNotNull { case ->
                when {
                    name == case.name -> case
                    !name.startsWith(case.name) -> null
                    name.removePrefix(case.name).firstOrNull() == '#' -> case
                    else -> null
                }
            }.takeIf { it.isNotEmpty() }?.let { test.copy(cases = it) }
        }.takeIf { it.isNotEmpty() }?.let { app.copy(tests = it) }
    }

private val Instrument.Status.Details.fullTestName get() = "$className#$testName"
