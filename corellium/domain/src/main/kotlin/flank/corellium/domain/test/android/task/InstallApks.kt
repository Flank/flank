package flank.corellium.domain.test.android.task

import flank.corellium.api.AndroidApps
import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Authorize
import flank.corellium.domain.TestAndroid.InstallApks
import flank.corellium.domain.TestAndroid.InvokeDevices
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.shard.Shard
import kotlinx.coroutines.flow.collect

/**
 * The step is installing required software on android instances.
 */
internal val installApks = InstallApks from setOf(
    Authorize,
    PrepareShards,
    InvokeDevices,
) using context {
    require(shards.size <= ids.size) { "Not enough instances, required ${shards.size} but was $ids.size" }
    val apks = prepareApkToInstall()
    api.installAndroidApps(apks).collect { event -> InstallApks.Status(event).out() }
    // If tests will be executed too fast just after the
    // app installed, the instrumentation will fail
    // delay(4_000) TODO: for verification
}

private fun TestAndroid.Context.prepareApkToInstall(): List<AndroidApps> =
    shards.mapIndexed { index, list: List<Shard.App> ->
        AndroidApps(
            instanceId = ids[index],
            paths = list.flatMap { app -> app.tests.map { test -> test.name } + app.name }
        )
    }
