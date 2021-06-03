package ftl.run.model

import ftl.api.FileReference
import ftl.api.ShardChunks
import ftl.args.AndroidArgs
import ftl.args.IgnoredTestCases
import ftl.shard.Chunk

interface WithArgs {
    val args: AndroidArgs
}

sealed class AndroidTestContext : WithArgs

data class InstrumentationTestContext(
    val app: FileReference,
    val test: FileReference,
    val shards: List<Chunk> = emptyList(),
    val ignoredTestCases: IgnoredTestCases = emptyList(),
    val environmentVariables: Map<String, String> = emptyMap(),
    val testTargetsForShard: ShardChunks = emptyList(),
    override val args: AndroidArgs
) : AndroidTestContext()

data class RoboTestContext(
    val app: FileReference,
    val roboScript: FileReference,
    override val args: AndroidArgs
) : AndroidTestContext()

data class GameLoopContext(
    val app: FileReference,
    val scenarioLabels: List<String>,
    val scenarioNumbers: List<String>,
    override val args: AndroidArgs
) : AndroidTestContext()

data class SanityRoboTestContext(
    val app: FileReference,
    override val args: AndroidArgs
) : AndroidTestContext()
