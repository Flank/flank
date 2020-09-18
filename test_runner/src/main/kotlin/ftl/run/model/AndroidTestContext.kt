package ftl.run.model

import ftl.shard.Chunk
import ftl.args.IgnoredTestCases
import ftl.util.FileReference

sealed class AndroidTestContext

data class InstrumentationTestContext(
    val app: FileReference,
    val test: FileReference,
    val shards: List<Chunk> = emptyList(),
    val ignoredTestCases: IgnoredTestCases = emptyList(),
    val environmentVariables: Map<String, String> = emptyMap()
) : AndroidTestContext()

data class RoboTestContext(
    val app: FileReference,
    val roboScript: FileReference
) : AndroidTestContext()

data class SanityRoboTestContext(
    val app: FileReference
) : AndroidTestContext()
