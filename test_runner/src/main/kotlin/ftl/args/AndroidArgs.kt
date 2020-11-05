package ftl.args

import ftl.args.yml.AppTestPair
import ftl.args.yml.Type

data class AndroidArgs(
    val commonArgs: CommonArgs,
    val appApk: String?,
    val testApk: String?,
    val additionalApks: List<String>,
    val autoGoogleLogin: Boolean,
    val useOrchestrator: Boolean,
    val roboDirectives: List<FlankRoboDirective>,
    val roboScript: String?,
    val environmentVariables: Map<String, String>, // should not be printed, becuase could contains sensitive informations
    val grantPermissions: String?,
    val scenarioLabels: List<String>,
    val obbFiles: List<String>,
    val obbNames: List<String>,
    val performanceMetrics: Boolean,
    val numUniformShards: Int?,
    val testRunnerClass: String?,
    val testTargets: List<String>,
    val additionalAppTestApks: List<AppTestPair>,
    override val useLegacyJUnitResult: Boolean,
    val obfuscateDumpShards: Boolean,
    val testTargetsForShard: List<String>
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
      directories-to-pull: ${ArgsToString.listToString(directoriesToPull)}
      grant-permissions: $grantPermissions
      type: ${type?.ymlName}
      other-files: ${ArgsToString.mapToString(otherFiles)}
      scenario-numbers: ${ArgsToString.listToString(scenarioNumbers)}
      scenario-labels: ${ArgsToString.listToString(scenarioLabels)}
      obb-files: ${ArgsToString.listToString(obbFiles)}
      obb-names: ${ArgsToString.listToString(obbNames)}
      performance-metrics: $performanceMetrics
      num-uniform-shards: $numUniformShards
      test-runner-class: $testRunnerClass
      test-targets:${ArgsToString.listToString(testTargets)}
      robo-directives:${ArgsToString.objectsToString(roboDirectives)}
      robo-script: $roboScript
      device: ${ArgsToString.objectsToString(devices)}
      num-flaky-test-attempts: $flakyTestAttempts
      test-targets-for-shard: ${ArgsToString.listToString(testTargetsForShard)}

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
      disable-results-upload: $disableResultsUpload
      default-class-test-time: $defaultClassTestTime
   """.trimIndent()
    }
}

// Changes these based on type
val AndroidArgs.isDontAutograntPermissions
    get() = !(grantPermissions.isNotNull() && (grantPermissions.equals("all")))

val AndroidArgs.isInstrumentationTest
    get() = if (type != null) (type == Type.INSTRUMENTATION) else validateInstrumentation()

fun AndroidArgs.validateInstrumentation() = (appApk.isNotNull() && testApk.isNotNull() ||
        additionalAppTestApks.isNotEmpty() &&
        (appApk.isNotNull() || additionalAppTestApks.all { (app, _) -> app.isNotNull() }))

val AndroidArgs.isRoboTest
    get() = if (type != null) type == Type.ROBO else validateRobo()

fun AndroidArgs.validateRobo() = (appApk.isNotNull() && (roboDirectives.isNotEmpty() || roboScript.isNotNull()))

private val AndroidArgs.checkForSanityRobo
    get() = appApk.isNotNull() &&
        testApk.isNull() &&
        roboScript.isNull() &&
        additionalAppTestApks.isEmpty()

val AndroidArgs.isSanityRobo
    get() = if (type != null) (type == Type.ROBO) && checkForSanityRobo else checkForSanityRobo

val AndroidArgs.isGameLoop
    get() = if (type == null) false else (type == Type.GAMELOOP)

private fun String?.isNull() = this == null
private fun String?.isNotNull() = isNull().not()
