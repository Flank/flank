package flank.corellium.domain.run.test.android.step

import flank.corellium.api.AndroidInstance
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.InvokeDevices
import flank.corellium.domain.step
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList

/**
 * The step is invoking amount of instances equal to the previously calculated shards count.
 * After the successful finish, the instances should be ready tu use.
 *
 * require:
 * * [RunTestCorelliumAndroid.Context.prepareShards]
 *
 * updates:
 * * [RunTestCorelliumAndroid.State.ids]
 */
internal fun RunTestCorelliumAndroid.Context.invokeDevices() = step(InvokeDevices) { out ->
    val config = AndroidInstance.Config(
        amount = shards.size,
        gpuAcceleration = args.gpuAcceleration
    )
    copy(ids = api.invokeAndroidDevices(config)
        .onEach { event -> out(event) }
        .filterIsInstance<AndroidInstance.Event.Ready>()
        .map { instance -> instance.id }
        .toList()
    )
}
