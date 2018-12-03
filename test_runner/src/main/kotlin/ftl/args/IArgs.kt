package ftl.args

interface IArgs {
    // original YAML data
    val data: String

    // GcloudYml
    val resultsBucket: String
    val recordVideo: Boolean
    val testTimeout: String
    val async: Boolean
    val project: String
    val resultsHistoryName: String?

    // FlankYml
    val testShards: Int
    val repeatTests: Int
    val smartFlankGcsPath: String
    val testTargetsAlwaysRun: List<String>

    // computed property
    val testShardChunks: List<List<String>>
}
