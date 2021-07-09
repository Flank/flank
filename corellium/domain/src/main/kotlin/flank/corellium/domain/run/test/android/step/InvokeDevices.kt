package flank.corellium.domain.run.test.android.step

import flank.corellium.api.AndroidInstance
import flank.corellium.domain.RunTestCorelliumAndroid.Authorize
import flank.corellium.domain.RunTestCorelliumAndroid.InvokeDevices
import flank.corellium.domain.RunTestCorelliumAndroid.PrepareShards
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList

/**
 * The step is invoking amount of instances equal to the previously calculated shards count.
 * After the successful finish, the instances should be ready tu use.
 */
internal val invokeDevices = InvokeDevices from setOf(
    Authorize,
    PrepareShards
) using context {
    val config = AndroidInstance.Config(
        amount = shards.size,
        gpuAcceleration = args.gpuAcceleration
    )
    api.invokeAndroidDevices(config)
        .onEach { event -> InvokeDevices.Status(event).out() }
        .filterIsInstance<AndroidInstance.Event.Ready>()
        .map { instance -> instance.id }
        .toList()
}
