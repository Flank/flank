package ftl.args

import ftl.shard.Chunk

data class CalculateShardsResult(val shardChunks: List<Chunk>, val ignoredTestCases: IgnoredTestCases)
