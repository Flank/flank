package ftl.args

import ftl.args.yml.AppTestPair

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
      default-test-time: $defaultTestTime
      use-average-test-time-for-new-tests: $useAverageTestTimeForNewTests
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
    get() = appApk.isNotNull() &&
            testApk.isNotNull() ||
            additionalAppTestApks.isNotEmpty() &&
            (appApk.isNotNull() || additionalAppTestApks.all { (app, _) -> app.isNotNull() })

val AndroidArgs.isRoboTest
    get() = appApk.isNotNull() &&
            (roboDirectives.isNotEmpty() || roboScript.isNotNull())

val AndroidArgs.isSanityRobo
    get() = appApk.isNotNull() &&
            testApk.isNull() &&
            roboScript.isNull() &&
            additionalAppTestApks.isEmpty()

private fun String?.isNull() = this == null
private fun String?.isNotNull() = isNull().not()
