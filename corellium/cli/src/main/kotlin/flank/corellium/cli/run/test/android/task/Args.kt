package flank.corellium.cli.run.test.android.task

import flank.config.loadYaml
import flank.corellium.cli.RunTestCorelliumAndroidCommand.Companion.context
import flank.corellium.cli.RunTestCorelliumAndroidCommand.Config
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import flank.exection.parallel.from
import flank.exection.parallel.using

val args = Args from setOf(Config) using context {
    Args(
        credentials = loadYaml(config.auth!!),
        apks = config.apks!!,
        testTargets = config.testTargets!!,
        maxShardsCount = config.maxTestShards!!,
        outputDir = config.localResultsDir!!,
        obfuscateDumpShards = config.obfuscate!!,
        gpuAcceleration = config.gpuAcceleration!!,
        scanPreviousDurations = config.scanPreviousDurations!!,
    )
}
