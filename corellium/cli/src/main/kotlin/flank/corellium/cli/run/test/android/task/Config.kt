package flank.corellium.cli.run.test.android.task

import flank.config.loadYaml
import flank.config.merge
import flank.corellium.cli.RunTestCorelliumAndroidCommand.Companion.context
import flank.corellium.cli.RunTestCorelliumAndroidCommand.Config
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.exection.parallel.using

internal val config = Config using context {
    merge(
        defaultConfig(),
        yamlConfig(command.yamlConfigPath),
        command.cliConfig
    )
}

private fun defaultConfig() = Config().apply {
    project = RunTestCorelliumAndroid.Args.DEFAULT_PROJECT
    auth = RunTestCorelliumAndroid.Args.AUTH_FILE
    this += RunTestCorelliumAndroid.Args.Default
}

private operator fun Config.plusAssign(args: RunTestCorelliumAndroid.Args) {
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
