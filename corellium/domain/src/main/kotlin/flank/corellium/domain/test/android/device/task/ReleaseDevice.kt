package flank.corellium.domain.test.android.device.task

import flank.corellium.domain.TestAndroid.AvailableDevices
import flank.corellium.domain.TestAndroid.Device
import flank.corellium.domain.TestAndroid.ExecuteTestShard
import flank.corellium.domain.TestAndroid.InstallApks
import flank.corellium.domain.TestAndroid.ReleaseDevice
import flank.exection.parallel.from
import flank.exection.parallel.using

/**
 * Makes device available again by adding it device channel.
 */
internal val releaseDevice = ReleaseDevice from setOf(
    InstallApks,
    ExecuteTestShard,
    AvailableDevices,
) using Device.context {
    release.send(device + installedApks)
}

private operator fun Device.Instance.plus(apks: Iterable<String>) =
    copy(
        apks = this.apks + apks,
        releaseTime = System.currentTimeMillis(),
    )
