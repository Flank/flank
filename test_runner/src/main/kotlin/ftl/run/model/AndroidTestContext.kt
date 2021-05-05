package ftl.run.model

import ftl.api.FileReference
import ftl.args.IgnoredTestCases
import ftl.args.ShardChunks
import ftl.shard.Chunk

sealed class AndroidTestContext

data class InstrumentationTestContext(
    val app: FileReference,
    val test: FileReference,
    val shards: List<Chunk> = emptyList(),
    val ignoredTestCases: IgnoredTestCases = emptyList(),
    val environmentVariables: Map<String, String> = emptyMap(),
    val testTargetsForShard: ShardChunks = emptyList(),
    val maxTestShards: Int = 0,
    val clientDetails: Map<String, String> = emptyMap()
) : AndroidTestContext()

data class RoboTestContext(
    val app: FileReference,
    val roboScript: FileReference
) : AndroidTestContext()

data class GameLoopContext(
    val app: FileReference,
    val scenarioLabels: List<String>,
    val scenarioNumbers: List<String>,
) : AndroidTestContext()

data class SanityRoboTestContext(
    val app: FileReference
) : AndroidTestContext()
