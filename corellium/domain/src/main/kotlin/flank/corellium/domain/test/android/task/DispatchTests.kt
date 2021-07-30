package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.Authorize
import flank.corellium.domain.TestAndroid.AvailableDevices
import flank.corellium.domain.TestAndroid.Device
import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.ExecuteTestShard
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.ParseApkInfo
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.invoke
import flank.exection.parallel.plus
import flank.exection.parallel.select
import flank.exection.parallel.using
import flank.exection.parallel.verify
import flank.shard.InstanceShard
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

/**
 * Dispatches each test shard execution into the first best matching device, and collects results.
 */
val dispatchTests = Dispatch.Tests from setOf(
    ParseApkInfo,
    Authorize,
    PrepareShards,
    AvailableDevices,
    Dispatch.Shards,
    ExecuteTests.Results,
) using context { state ->
    channelFlow {
        var running = 0
        dispatch.consumeEach { data: Dispatch.Data ->
            ++running
            val instance = devices.maxBy { instance ->
                (data.shard.apks intersect instance.apks).size
            }
            val seed = mapOf(
                Dispatch.Data + data,
                Device.Instance + instance
            )
            ExecuteTests.Dispatch(instance.id, data).out()

            launch {
                Device
                    .execute(state + seed)
                    .last()
                    .verify()
                    .select(ExecuteTestShard)
                    .let { send(it) }

                if (--running == 0) {
                    results.close()
                    dispatch.close()
                }
            }
        }
    }.toList().reduce { accumulator, value ->
        accumulator + value
    }
}

/**
 * Flatten [InstanceShard] to the apk names.
 */
private val InstanceShard.apks: List<String>
    get() = flatMap { app -> app.tests.map { test -> test.name } + app.name }

/**
 * Receives all pending elements and returns one represented by the biggest number returned from [select] function.
 * The rest of the received values will be added back to the channel.
 */
private suspend fun <T> Channel<T>.maxBy(select: (T) -> Int): T =
    mutableListOf<T>().apply {
        add(receive())
        while (!isEmpty) add(receive())
        sortByDescending(select)
        drop(1).forEach { send(it) }
    }.first()
