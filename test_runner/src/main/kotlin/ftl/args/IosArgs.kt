package ftl.args

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.annotations.VisibleForTesting
import flank.tool.analytics.AnonymizeInStatistics
import ftl.args.yml.Type
import ftl.ios.xctest.XcTestRunData
import ftl.ios.xctest.calculateXcTestRunData
import ftl.ios.xctest.common.XctestrunMethods
import ftl.run.IOS_SHARD_FILE
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

data class IosArgs(
    @get:JsonIgnore
    val commonArgs: CommonArgs,

    @property:AnonymizeInStatistics
    val xctestrunZip: String,

    @property:AnonymizeInStatistics
    val xctestrunFile: String,
    val xcodeVersion: String?,

    @property:AnonymizeInStatistics
    val testTargets: List<String>,
    val obfuscateDumpShards: Boolean,

    @property:AnonymizeInStatistics
    val additionalIpas: List<String>,

    @property:AnonymizeInStatistics
    val app: String,
    val testSpecialEntitlements: Boolean?,

    @property:AnonymizeInStatistics
    val onlyTestConfiguration: String,

    @property:AnonymizeInStatistics
    val skipTestConfiguration: String
) : IArgs by commonArgs {

    override val useLegacyJUnitResult = true

    @get:JsonIgnore
    val xcTestRunData: XcTestRunData by lazy { calculateXcTestRunData() }

    companion object : IosArgsCompanion()

    override fun toString(): String {
        return """
IosArgs
    gcloud:
      results-bucket: $resultsBucket
      results-dir: $resultsDir
      record-video: $recordVideo
      timeout: $testTimeout
      async: $async
      client-details:${ArgsToString.mapToString(clientDetails)}
      network-profile: $networkProfile
      results-history-name: $resultsHistoryName
      # iOS gcloud
      test: $xctestrunZip
      xctestrun-file: $xctestrunFile
      xcode-version: $xcodeVersion
      device:${ArgsToString.objectsToString(devices)}
      num-flaky-test-attempts: $flakyTestAttempts
      directories-to-pull:${ArgsToString.listToString(directoriesToPull)}
      other-files:${ArgsToString.mapToString(otherFiles)}
      additional-ipas:${ArgsToString.listToString(additionalIpas)}
      scenario-numbers:${ArgsToString.listToString(scenarioNumbers)}
      type: ${type?.ymlName}
      app: $app
      test-special-entitlements: $testSpecialEntitlements
      fail-fast: $failFast

    flank:
      max-test-shards: $maxTestShards
      shard-time: $shardTime
      num-test-runs: $repeatTests
      smart-flank-gcs-path: $smartFlankGcsPath
      smart-flank-disable-upload: $smartFlankDisableUpload
      default-test-time: $defaultTestTime
      use-average-test-time-for-new-tests: $useAverageTestTimeForNewTests
      test-targets-always-run:${ArgsToString.listToString(testTargetsAlwaysRun)}
      files-to-download:${ArgsToString.listToString(filesToDownload)}
      keep-file-path: $keepFilePath
      full-junit-result: $fullJUnitResult
      # iOS flank
      test-targets:${ArgsToString.listToString(testTargets)}
      disable-sharding: $disableSharding
      project: $project
      local-result-dir: $localResultDir
      run-timeout: $runTimeout
      ignore-failed-tests: $ignoreFailedTests
      output-style: ${outputStyle.name.lowercase()}
      disable-results-upload: $disableResultsUpload
      default-class-test-time: $defaultClassTestTime
      disable-usage-statistics: $disableUsageStatistics
      only-test-configuration: $onlyTestConfiguration
      skip-test-configuration: $skipTestConfiguration
      output-report: $outputReportType
      skip-config-validation: $skipConfigValidation
      custom-sharding-json: $customShardingJson
      ignore-global-tests: $ignoreNonGlobalTests
        """.trimIndent()
    }
}

val IosArgs.isXcTest: Boolean
    get() = type == Type.XCTEST

val IosArgs.shardsFilePath
    get() = Paths.get(localStorageDirectory, IOS_SHARD_FILE).toAbsolutePath().toString()

@VisibleForTesting
internal fun filterTests(
    validTestMethods: XctestrunMethods,
    testTargets: List<String>
): XctestrunMethods =
    if (testTargets.isEmpty()) validTestMethods
    else testTargets.map { testTarget ->
        try {
            testTarget.toRegex()
        } catch (e: Exception) {
            throw FlankConfigurationError("Invalid regex: $testTarget", e)
        }
    }.let { testTargetRgx ->
        validTestMethods.mapValues { (_, tests) ->
            tests.filter { test ->
                testTargetRgx.any { regex -> test.matches(regex) }
            }
        }
    }
