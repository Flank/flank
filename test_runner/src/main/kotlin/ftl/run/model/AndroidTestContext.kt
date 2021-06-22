package ftl.run.model

import ftl.api.FileReference
import ftl.api.ShardChunks
import ftl.args.AndroidArgs
import ftl.args.IgnoredTestCases
import ftl.shard.Chunk

sealed class AndroidTestContext(open val args: AndroidArgs)

data class InstrumentationTestContext(
    val app: FileReference,
    val test: FileReference,
    val shards: List<Chunk> = emptyList(),
    val ignoredTestCases: IgnoredTestCases = emptyList(),
    val environmentVariables: Map<String, String> = emptyMap(),
    val testTargetsForShard: ShardChunks = emptyList(),
    val parameterizedTestsOption: String = "",
    override val args: AndroidArgs
) : AndroidTestContext(args)

data class RoboTestContext(
    val app: FileReference,
    val roboScript: FileReference,
    override val args: AndroidArgs
) : AndroidTestContext(args)

data class GameLoopContext(
    val app: FileReference,
    val scenarioLabels: List<String>,
    val scenarioNumbers: List<String>,
    override val args: AndroidArgs
) : AndroidTestContext(args)

data class SanityRoboTestContext(
    val app: FileReference,
    override val args: AndroidArgs
) : AndroidTestContext(args)
