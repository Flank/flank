package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.DeviceCost
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using

val fetchDeviceCostPerSecond = DeviceCost from setOf(
    TestAndroid.InvokeDevices
) using context {
    ids.associateWith(api.getRate).mapValues { (_, value) -> value.await().onRateMicroCents }
}
