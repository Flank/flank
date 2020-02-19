package ftl.args

import ftl.args.yml.FlankYmlParams

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

    fun useLocalResultDir() = localResultDir != FlankYmlParams.defaultLocalResultDir

    companion object {
        // num_shards must be > 1, and <= 50
        val AVAILABLE_SHARD_COUNT_RANGE = 1..50
    }
}
