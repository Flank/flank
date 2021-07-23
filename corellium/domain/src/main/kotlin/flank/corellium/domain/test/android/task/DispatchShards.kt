package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.shard.Shard
import kotlinx.coroutines.channels.Channel

/**
 * Prepares channel for dispatching shards to run and fills the buffer with initial elements.
 */
val dispatchShards = Dispatch.Shards from setOf(
    PrepareShards
) using TestAndroid.context {
    Channel<Dispatch.Data>(bufferSize).apply {
        shards.forEachIndexed { index, shard ->
            send(
                Dispatch.Data(
                    index = index,
                    shard = shard,
                    type = Dispatch.Type.Shard,
                )
            )
        }
    }
}

/**
 * Calculates buffer size for [Dispatch.Data].
 * Proper size is crucial for avoiding deadlock on dispatch channel.
 * The safe size should be large enough to keep all possible events at single time.
 */
private val TestAndroid.Context.bufferSize: Int
    get() = shards.flatten().flatMap(Shard.App::tests)
        .size * args.flakyTestsAttempts +
        shards.size * EVENT_COUNT_FOR_SHARD

/**
 * The expected amount of events that can be emitted per one shard.
 */
private val EVENT_COUNT_FOR_SHARD = setOf(
    ExecuteTests.Dispatch::class,
    ExecuteTests.Finish::class,
).size
