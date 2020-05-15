package ftl.args

import ftl.args.yml.FlankYmlParams
import ftl.run.status.OutputStyle
import ftl.util.timeoutToMils

// Properties common to both Android and iOS
interface IArgs {
    // original YAML data
    val data: String

    // GcloudYml
    val resultsBucket: String
    val resultsDir: String?
    val recordVideo: Boolean
    val testTimeout: String
    val async: Boolean
    val clientDetails: Map<String, String>?
    val networkProfile: String?
    val project: String
    val resultsHistoryName: String?
    val flakyTestAttempts: Int

    // FlankYml
    val maxTestShards: Int
    val shardTime: Int
    val repeatTests: Int
    val smartFlankGcsPath: String
    val smartFlankDisableUpload: Boolean
    val testTargetsAlwaysRun: List<String>
    val filesToDownload: List<String>
    val disableSharding: Boolean
    val localResultDir: String
    val runTimeout: String
    val parsedTimeout: Long
        get() = timeoutToMils(runTimeout).let {
            if (it < 0) Long.MAX_VALUE
            else it
        }
    val useLegacyJUnitResult: Boolean get() = false
    val ignoreFailedTests: Boolean
    val keepFilePath: Boolean
    val outputStyle: OutputStyle
    val defaultOutputStyle
        get() = if (flakyTestAttempts > 0 || (!disableSharding && maxTestShards > 0))
            OutputStyle.Multi else
            OutputStyle.Verbose

    fun useLocalResultDir() = localResultDir != FlankYmlParams.defaultLocalResultsDir

    companion object {
        // num_shards must be >= 1, and <= 50
        val AVAILABLE_SHARD_COUNT_RANGE = 1..50
    }
}
