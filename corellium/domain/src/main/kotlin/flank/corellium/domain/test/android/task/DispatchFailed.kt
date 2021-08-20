package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.shard.InstanceShard
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.atomic.AtomicInteger

/**
 * Collects each [ExecuteTests.Results] and checks its status.
 * Each failed test is dispatched again at most as many times as specified in flakyTestsAttempts argument.
 * Rerunning failed tests help detect flakiness.
 */
internal val dispatchFailedTests = Dispatch.Failed from setOf(
    Dispatch.Shards,
    ExecuteTests.Results,
) using context {
    val counter = { AtomicInteger(0) }
    val runs = mutableMapOf<InstanceShard, AtomicInteger>()
    var index = 0
    results.consumeEach { result ->
        if (result.status is Instrument.Status) {
            if (result.status.code in Instrument.Code.errors) {
                val shard = result.shard.reduceTo(result.status.name)
                val attempt = runs.getOrPut(shard, counter).getAndIncrement()
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
 * Creates new [InstanceShard] that contains only one test basing on the given [name].
 *
 * If the given [name] refers parameterized test method, the algorithm will return [InstanceShard] with a test case for the whole class instead of the test case method.
 * This behaviour is required because parameterized test methods are produced in runtime based on annotations and cannot be run separately due to limitations of android instrumentation.
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
