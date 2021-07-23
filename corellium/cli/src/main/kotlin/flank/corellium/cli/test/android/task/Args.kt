package flank.corellium.cli.test.android.task

import flank.config.loadYaml
import flank.corellium.cli.TestAndroidCommand.Companion.context
import flank.corellium.cli.TestAndroidCommand.Config
import flank.corellium.domain.TestAndroid.Args
import flank.exection.parallel.from
import flank.exection.parallel.using

internal val args = Args from setOf(Config) using context {
    Args(
        credentials = loadYaml(config.auth!!),
        apks = config.apks!!,
        testTargets = config.testTargets!!,
        maxShardsCount = config.maxTestShards!!,
        outputDir = config.localResultsDir!!,
        obfuscateDumpShards = config.obfuscate!!,
        gpuAcceleration = config.gpuAcceleration!!,
        scanPreviousDurations = config.scanPreviousDurations!!,
        flakyTestsAttempts = config.flakyTestAttempts!!,
    )
}
