package ftl.args

import ftl.config.Device
import ftl.run.status.OutputStyle

data class CommonArgs(
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
    override val defaultTestClassTime: Double,
    override val useAverageTestTimeForNewTests: Boolean,
) : IArgs
