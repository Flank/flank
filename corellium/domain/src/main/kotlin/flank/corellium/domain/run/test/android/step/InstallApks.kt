package flank.corellium.domain.run.test.android.step

import flank.corellium.api.AndroidApps
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.InstallApks
import flank.corellium.domain.step
import flank.shard.Shard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

/**
 * The step is installing required software on android instances.
 *
 * require:
 * * [RunTestCorelliumAndroid.Context.authorize]
 * * [RunTestCorelliumAndroid.Context.prepareShards]
 * * [RunTestCorelliumAndroid.Context.invokeDevices]
 */
internal fun RunTestCorelliumAndroid.Context.installApks() = step(InstallApks) { out ->
    require(shards.size <= ids.size) { "Not enough instances, required ${shards.size} but was $ids.size" }
    val apks = prepareApkToInstall()
    api.installAndroidApps(apks).collect { event -> event.out() }
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
