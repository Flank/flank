package ftl.run.model

import ftl.args.IgnoredTestCases
import ftl.json.MatrixMap
import ftl.args.ShardChunks

data class TestResult(
    val matrixMap: MatrixMap,
    val shardChunks: ShardChunks,
    val ignoredTests: IgnoredTestCases = emptyList()
)
