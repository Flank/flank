package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.shard.Shard
import kotlinx.coroutines.channels.Channel

/**
 * Creates channel for dispatching and receiving [TestAndroid.Device.Result].
 *
 * Required for flaky tests detection.
 */
internal val initResultsChannel = ExecuteTests.Results from setOf(
    PrepareShards
) using context {
    Channel(bufferSize)
}

private val TestAndroid.Context.bufferSize: Int
    get() = shards.flatten().flatMap(Shard.App::tests).size
