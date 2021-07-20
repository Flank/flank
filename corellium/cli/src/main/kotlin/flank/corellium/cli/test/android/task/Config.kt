package flank.corellium.cli.test.android.task

import flank.config.loadYaml
import flank.config.merge
import flank.corellium.cli.TestAndroidCommand.Companion.context
import flank.corellium.cli.TestAndroidCommand.Config
import flank.corellium.domain.TestAndroid
import flank.exection.parallel.using

internal val config = Config using context {
    merge(
        defaultConfig(),
        yamlConfig(command.yamlConfigPath),
        command.cliConfig
    )
}

private fun defaultConfig() = Config().apply {
    project = TestAndroid.Args.DEFAULT_PROJECT
    auth = TestAndroid.Args.AUTH_FILE
    this += TestAndroid.Args.Default
}

private operator fun Config.plusAssign(args: TestAndroid.Args) {
    apks = args.apks
    testTargets = args.testTargets
    maxTestShards = args.maxShardsCount
    localResultsDir = args.outputDir
    obfuscate = args.obfuscateDumpShards
    gpuAcceleration = args.gpuAcceleration
    scanPreviousDurations = args.scanPreviousDurations
}

internal fun yamlConfig(path: String?): Config =
    path?.let(::loadYaml) ?: Config()
