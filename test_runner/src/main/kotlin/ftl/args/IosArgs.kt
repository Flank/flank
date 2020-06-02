package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.config.Device
import ftl.ios.Xctestrun
import ftl.util.FlankTestMethod

data class IosArgs(
    val commonArgs: CommonArgs,
    val xctestrunZip: String,
    val xctestrunFile: String,
    val xcodeVersion: String?,
    val devices: List<Device>,
    val testTargets: List<String>
) : IArgs by commonArgs {

    override val useLegacyJUnitResult = true
    val testShardChunks: ShardChunks by lazy { calculateShardChunks() }

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
      client-details: ${ArgsToString.mapToString(clientDetails)}
      network-profile: $networkProfile
      results-history-name: $resultsHistoryName
      # iOS gcloud
      test: $xctestrunZip
      xctestrun-file: $xctestrunFile
      xcode-version: $xcodeVersion
      device:${ArgsToString.objectsToString(devices)}
      num-flaky-test-attempts: $flakyTestAttempts

    flank:
      max-test-shards: $maxTestShards
      shard-time: $shardTime
      num-test-runs: $repeatTests
      smart-flank-gcs-path: $smartFlankGcsPath
      smart-flank-disable-upload: $smartFlankDisableUpload
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
      output-style: ${outputStyle.name.toLowerCase()}
    """.trimIndent()
    }
}

private fun IosArgs.calculateShardChunks() = if (disableSharding)
    listOf(emptyList()) else
    ArgsHelper.calculateShards(
        filterTests(
            Xctestrun.findTestNames(xctestrunFile),
            testTargets
        ).distinct().map {
            FlankTestMethod(it, ignored = false)
        },
        this
    )

@VisibleForTesting
internal fun filterTests(validTestMethods: List<String>, testTargetsRgx: List<String?>): List<String> {
    if (testTargetsRgx.isEmpty()) {
        return validTestMethods
    }

    return validTestMethods.filter { test ->
        testTargetsRgx.filterNotNull().forEach { target ->
            try {
                if (test.matches(target.toRegex())) {
                    return@filter true
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid regex: $target", e)
            }
        }

        return@filter false
    }
}
