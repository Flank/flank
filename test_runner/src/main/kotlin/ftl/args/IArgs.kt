package ftl.args

import flank.common.OutputLogLevel
import flank.common.config.isTest
import flank.common.setLogLevel
import flank.tool.analytics.mixpanel.AnonymizeInStatistics
import flank.tool.analytics.mixpanel.IgnoreInStatistics
import ftl.args.yml.Type
import ftl.config.Device
import ftl.config.common.CommonFlankConfig.Companion.defaultLocalResultsDir
import ftl.reports.output.OutputReportType
import ftl.run.status.OutputStyle
import ftl.util.timeoutToMils
import java.nio.file.Paths

// Properties common to both Android and iOS
interface IArgs {
    // original YAML data
    @IgnoreInStatistics
    val data: String

    // GcloudYml
    val devices: List<Device>

    @AnonymizeInStatistics
    val resultsBucket: String

    @AnonymizeInStatistics
    val resultsDir: String

    val recordVideo: Boolean
    val testTimeout: String
    val async: Boolean

    @AnonymizeInStatistics
    val clientDetails: Map<String, String>?
    val networkProfile: String?
    val project: String
    val resultsHistoryName: String?
    val flakyTestAttempts: Int

    @AnonymizeInStatistics
    val otherFiles: Map<String, String>
    val scenarioNumbers: List<String>
    val type: Type? get() = null

    @AnonymizeInStatistics
    val directoriesToPull: List<String>
    val failFast: Boolean

    // FlankYml
    val maxTestShards: Int
    val shardTime: Int
    val repeatTests: Int

    @AnonymizeInStatistics
    val smartFlankGcsPath: String
    val smartFlankDisableUpload: Boolean
    val testTargetsAlwaysRun: List<String>

    @AnonymizeInStatistics
    val filesToDownload: List<String>
    val disableSharding: Boolean

    @AnonymizeInStatistics
    val localResultDir: String
    val runTimeout: String
    val parsedTimeout: Long
        get() = timeoutToMils(runTimeout).let {
            if (it < 0) Long.MAX_VALUE
            else it
        }
    val useLegacyJUnitResult: Boolean get() = false
    val fullJUnitResult: Boolean get() = false
    val ignoreFailedTests: Boolean
    val keepFilePath: Boolean
    val outputStyle: OutputStyle
    val defaultOutputStyle
        get() = if (hasMultipleExecutions)
            OutputStyle.Multi else
            OutputStyle.Verbose
    val hasMultipleExecutions
        get() = flakyTestAttempts > 0 || (!disableSharding && maxTestShards > 0)

    val disableResultsUpload: Boolean get() = false

    val inPhysicalRange: Boolean
        get() = maxTestShards in AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE

    val inVirtualRange: Boolean
        get() = maxTestShards in AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE

    val defaultTestTime: Double
    val defaultClassTestTime: Double
    val useAverageTestTimeForNewTests: Boolean

    val disableUsageStatistics: Boolean

    val outputReportType: OutputReportType
    val skipConfigValidation: Boolean
    val shouldValidateConfig: Boolean
        get() = !skipConfigValidation

    @AnonymizeInStatistics
    val customShardingJson: String

    fun useLocalResultDir() = localResultDir != defaultLocalResultsDir

    companion object {
        // num_shards must be >= 1, and <= 50
        val AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE = 1..50

        val AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE = 1..500
    }

    interface ICompanion {
        val validArgs: Map<String, List<String>>
    }
}

val IArgs.logLevel
    get() = if (outputStyle == OutputStyle.Compact) OutputLogLevel.SIMPLE else OutputLogLevel.DETAILED

fun IArgs.setupLogLevel() {
    setLogLevel(logLevel)
}

val IArgs.blockSendingUsageStatistics
    get() = disableUsageStatistics || isTest()

val IArgs.localStorageDirectory
    get() = if (useLocalResultDir()) localResultDir else Paths.get(localResultDir, resultsDir).toString()
