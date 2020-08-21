package ftl.run.model

import ftl.args.IgnoredTestCases
import ftl.args.ShardChunks
import ftl.json.MatrixMap

data class TestResult(
    val matrixMap: MatrixMap,
    val shardChunks: ShardChunks,
    val ignoredTests: IgnoredTestCases = emptyList()
)
