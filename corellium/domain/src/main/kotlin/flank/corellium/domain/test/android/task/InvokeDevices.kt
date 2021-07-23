package flank.corellium.domain.test.android.task

import flank.corellium.api.AndroidInstance
import flank.corellium.domain.TestAndroid.Authorize
import flank.corellium.domain.TestAndroid.AvailableDevices
import flank.corellium.domain.TestAndroid.Device
import flank.corellium.domain.TestAndroid.InvokeDevices
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList

/**
 * Invokes amount of instances equal to the previously calculated shards count.
 * Each instance ready to use is immediately sent into device channel.
 * After the successful finish, all instances should be ready tu use.
 */
internal val invokeDevices = InvokeDevices from setOf(
    Authorize,
    PrepareShards,
    AvailableDevices,
) using context {
    val config = AndroidInstance.Config(
        amount = shards.size,
        gpuAcceleration = args.gpuAcceleration
    )
    api.invokeAndroidDevices(config)
        .onEach { event -> InvokeDevices.Status(event).out() }
        .filterIsInstance<AndroidInstance.Event.Ready>()
        .map { instance -> instance.id }
        .onEach { id -> devices.send(Device.Instance(id)) }
        .toList()
}
