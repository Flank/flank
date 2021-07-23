package flank.corellium.domain.test.android.device.task

import flank.corellium.api.AndroidApps
import flank.corellium.domain.TestAndroid.Authorize
import flank.corellium.domain.TestAndroid.Device
import flank.corellium.domain.TestAndroid.InstallApks
import flank.exection.parallel.from
import flank.exection.parallel.using
import kotlinx.coroutines.flow.collect

/**
 * Installs the required software on android instance.
 */
internal val installApks = InstallApks from setOf(
    Authorize,
) using Device.context {
    val apks = requiredApks() - device.apks
    if (apks.isNotEmpty()) {
        val config = listOf(AndroidApps(device.id, apks))
        api.installAndroidApps(config).collect { event ->
            InstallApks.Status(event).out()
        }
    }
    apks
    // If tests will be executed too fast just after the
    // app installed, the instrumentation will fail
    // delay(4_000) TODO: for verification
}

private fun Device.Context.requiredApks(): List<String> =
    shard.flatMap { app ->
        app.tests.map { test -> test.name } + app.name
    }
