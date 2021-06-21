package ftl.args

import ftl.args.yml.Type
import ftl.config.Device
import ftl.reports.output.OutputReportType
import ftl.run.status.OutputStyle

data class CommonArgs(
    @Transient // we don't want to have this field saved in outputReport.json
    override val data: String,

    // Gcloud
    override val devices: List<Device>,
    override val resultsBucket: String,
    override val resultsDir: String,
    override val recordVideo: Boolean,
    override val testTimeout: String,
    override val async: Boolean,
    override val resultsHistoryName: String?,
    override val flakyTestAttempts: Int,
    override val clientDetails: Map<String, String>?,
    override val networkProfile: String?,
    override val otherFiles: Map<String, String>,
    override val type: Type?,
    override val directoriesToPull: List<String>,
    override val scenarioNumbers: List<String>,
    override val failFast: Boolean,

    // flank
    override val project: String,
    override val maxTestShards: Int,
    override val shardTime: Int,
    override val repeatTests: Int,
    override val smartFlankGcsPath: String,
    override val smartFlankDisableUpload: Boolean,
    override val testTargetsAlwaysRun: List<String>,
    override val filesToDownload: List<String>,
    override val disableSharding: Boolean,
    override val localResultDir: String,
    override val runTimeout: String,
    override val fullJUnitResult: Boolean,
    override val ignoreFailedTests: Boolean,
    override val keepFilePath: Boolean,
    override val outputStyle: OutputStyle,
    override val disableResultsUpload: Boolean,
    override val defaultTestTime: Double,
    override val defaultClassTestTime: Double,
    override val useAverageTestTimeForNewTests: Boolean,
    override val disableUsageStatistics: Boolean,
    override val outputReportType: OutputReportType,
    override val skipConfigValidation: Boolean,
    override val customShardingJson: String
) : IArgs
