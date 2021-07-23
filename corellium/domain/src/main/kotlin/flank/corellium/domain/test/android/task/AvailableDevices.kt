package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.AvailableDevices
import flank.exection.parallel.from
import flank.exection.parallel.using
import kotlinx.coroutines.channels.Channel

/**
 * Prepares channel for providing devices ready to use.
 */
val availableDevices = AvailableDevices from setOf(
    TestAndroid.PrepareShards
) using TestAndroid.context {
    Channel(shards.size)
}
