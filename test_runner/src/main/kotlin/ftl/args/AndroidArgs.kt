package ftl.args

import ftl.args.yml.AppTestPair
import ftl.config.resolve
import ftl.config.isVirtual

data class AndroidArgs(
    val commonArgs: CommonArgs,
    val appApk: String?,
    val testApk: String?,
    val additionalApks: List<String>,
    val autoGoogleLogin: Boolean,
    val useOrchestrator: Boolean,
    val roboDirectives: List<FlankRoboDirective>,
    val roboScript: String?,
    val environmentVariables: Map<String, String>,
    val directoriesToPull: List<String>,
    val otherFiles: Map<String, String>,
    val performanceMetrics: Boolean,
    val numUniformShards: Int?,
    val testRunnerClass: String?,
    val testTargets: List<String>,
    val additionalAppTestApks: List<AppTestPair>,
    override val useLegacyJUnitResult: Boolean
) : IArgs by commonArgs {
    companion object : AndroidArgsCompanion()

    // override val maxTestShards: Int = calculateMaxTestShards(commonArgs.maxTestShards)
    override fun toString(): String {
        return """
AndroidArgs
    gcloud:
      results-bucket: $resultsBucket
      results-dir: $resultsDir
      record-video: $recordVideo
      timeout: $testTimeout
      async: $async
      client-details: ${ArgsToString.mapToString(clientDetails)}
      network-profile: $networkProfile
      results-history-name: $resultsHistoryName
      # Android gcloud
      app: $appApk
      test: $testApk
      additional-apks: ${ArgsToString.listToString(additionalApks)}
      auto-google-login: $autoGoogleLogin
      use-orchestrator: $useOrchestrator
      directories-to-pull:${ArgsToString.listToString(directoriesToPull)}
      other-files:${ArgsToString.mapToString(otherFiles)}
      performance-metrics: $performanceMetrics
      num-uniform-shards: $numUniformShards
      test-runner-class: $testRunnerClass
      test-targets:${ArgsToString.listToString(testTargets)}
      robo-directives:${ArgsToString.objectsToString(roboDirectives)}
      robo-script: $roboScript
      device:${ArgsToString.objectsToString(devices)}
      num-flaky-test-attempts: $flakyTestAttempts

    flank:
      max-test-shards: $maxTestShards
      shard-time: $shardTime
      num-test-runs: $repeatTests
      smart-flank-gcs-path: $smartFlankGcsPath
      smart-flank-disable-upload: $smartFlankDisableUpload
      files-to-download:${ArgsToString.listToString(filesToDownload)}
      test-targets-always-run:${ArgsToString.listToString(testTargetsAlwaysRun)}
      disable-sharding: $disableSharding
      project: $project
      local-result-dir: $localResultDir
      full-junit-result: $fullJUnitResult
      # Android Flank Yml
      keep-file-path: $keepFilePath
      additional-app-test-apks:${ArgsToString.apksToString(additionalAppTestApks)}
      run-timeout: $runTimeout
      legacy-junit-result: $useLegacyJUnitResult
      ignore-failed-tests: $ignoreFailedTests
      output-style: ${outputStyle.name.toLowerCase()}
   """.trimIndent()
    }
}

val AndroidArgs.isInstrumentationTest
    get() = appApk != null && testApk != null ||
            additionalAppTestApks.isNotEmpty() &&
            (appApk != null || additionalAppTestApks.all { (app, _) -> app != null })

val AndroidArgs.isRoboTest
    get() = appApk != null &&
            (roboDirectives.isNotEmpty() || roboScript != null)

fun AndroidArgs.containsVirtualDevices() = devices.any { it.isVirtual!! }

fun AndroidArgs.containsPhysicalDevices() = devices.any { !it.isVirtual!! }

fun AndroidArgs.shouldSplitRuns() = containsPhysicalDevices() && maxTestShards > 50

fun AndroidArgs.splitConfigurationByDeviceType() = listOf(
    this.copy(commonArgs = commonArgs.copy(devices = devices.filter { it.isVirtual ?: false }, maxTestShards = maxTestShards)),
    this.copy(commonArgs = commonArgs.copy(devices = devices.filter { !(it.isVirtual ?: false) }, maxTestShards = maxTestShards.scaleToPhysicalShardsCount()))
)

private fun Int.scaleToPhysicalShardsCount() = if (this !in IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE)
    IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last else this
