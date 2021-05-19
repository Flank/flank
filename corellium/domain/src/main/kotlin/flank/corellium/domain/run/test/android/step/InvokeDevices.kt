package flank.corellium.domain.run.test.android.step

import flank.corellium.api.AndroidInstance
import flank.corellium.domain.RunTestCorelliumAndroid
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
internal fun RunTestCorelliumAndroid.Context.invokeDevices() = RunTestCorelliumAndroid.step {
    println("* Invoking devices")
    copy(ids = api.invokeAndroidDevices(AndroidInstance.Config(shards.size)).toList())
}
