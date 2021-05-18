package flank.corellium.domain.run.test.android.step

import flank.corellium.api.AndroidApps
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.shard.Shard
import kotlinx.coroutines.delay

/**
 * The step is installing required software on android instances.
 *
 * require:
 * * [RunTestCorelliumAndroid.Context.authorize]
 * * [RunTestCorelliumAndroid.Context.prepareShards]
 * * [RunTestCorelliumAndroid.Context.invokeDevices]
 */
internal fun RunTestCorelliumAndroid.Context.installApks() = RunTestCorelliumAndroid.step {
    println("* Installing apks")
    require(shards.size <= ids.size) { "Not enough instances, required ${shards.size} but was $ids.size" }
    val apks = prepareApkToInstall()
    api.installAndroidApps(apks).join()
    // If tests will be executed too fast just after the
    // app installed, the instrumentation will fail
    delay(8_000)
    this
}

private fun RunTestCorelliumAndroid.State.prepareApkToInstall(): List<AndroidApps> =
    shards.mapIndexed { index, list: List<Shard.App> ->
        AndroidApps(
            instanceId = ids[index],
            paths = list.flatMap { app -> app.tests.map { test -> test.name } + app.name }
        )
    }
