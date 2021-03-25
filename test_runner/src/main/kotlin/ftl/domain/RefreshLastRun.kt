package ftl.domain

import ftl.args.AndroidArgs
import ftl.run.refreshLastRun

interface RefreshLastRun

suspend operator fun RefreshLastRun.invoke() {
    refreshLastRun(
        currentArgs = AndroidArgs.default(),
        testShardChunks = emptyList()
    )
}
