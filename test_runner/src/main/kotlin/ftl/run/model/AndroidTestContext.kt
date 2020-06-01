package ftl.run.model

import ftl.args.ShardChunks
import ftl.util.FileReference

sealed class AndroidTestContext

data class InstrumentationTestContext(
    val app: FileReference,
    val test: FileReference,
    val shards: ShardChunks = emptyList()
) : AndroidTestContext()

data class RoboTestContext(
    val app: FileReference,
    val roboScript: FileReference
) : AndroidTestContext()
