package ftl.args

import java.nio.file.Path

interface IArgs {
    var filePath: Path?

    // GcloudYml
    val resultsBucket: String
    val recordVideo: Boolean
    val testTimeout: String
    val async: Boolean
    val projectId: String
    val resultsHistoryName: String?

    // FlankYml
    val testShards: Int
    val repeatTests: Int
    val testTargetsAlwaysRun: List<String>

    // computed property
    val testShardChunks: List<List<String>>
}
