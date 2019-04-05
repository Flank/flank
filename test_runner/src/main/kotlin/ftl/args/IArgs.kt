package ftl.args

import ftl.args.yml.FlankYmlParams

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

    // computed property
    val testShardChunks: List<List<String>>

    fun useLocalResultDir() = localResultDir != FlankYmlParams.defaultLocalResultDir
}
