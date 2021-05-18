package ftl.run.model

import ftl.api.ShardChunks
import ftl.args.IgnoredTestCases
import ftl.json.MatrixMap

data class TestResult(
    val matrixMap: MatrixMap,
    val shardChunks: ShardChunks,
    val ignoredTests: IgnoredTestCases = emptyList()
)
