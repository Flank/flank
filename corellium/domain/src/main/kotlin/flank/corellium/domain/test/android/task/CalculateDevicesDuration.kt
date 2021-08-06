package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.AvailableDevices
import flank.corellium.domain.TestAndroid.DevicesDuration
import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import kotlinx.coroutines.channels.toList

val calculateDevicesDuration = DevicesDuration from setOf(
    AvailableDevices,
    Dispatch.Tests,
) using context {
    devices.close()
    devices.toList().associate { device ->
        device.run { id to releaseTime - startTime }
    }
}
