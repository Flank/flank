package ftl.domain

import ftl.args.AndroidArgs
import ftl.presentation.Output
import ftl.presentation.RunState
import ftl.presentation.runBlockingWithObservingRunState
import ftl.run.refreshLastRun

interface RefreshLastRun : Output

operator fun RefreshLastRun.invoke() {
    runBlockingWithObservingRunState {
        refreshLastRun(
            currentArgs = AndroidArgs.default(),
            testShardChunks = emptyList()
        )
    }
}

sealed interface RefreshLastRunState : RunState {
    data class LoadingRun(val lastRun: String) : RefreshLastRunState
    object RefreshMatricesStarted : RefreshLastRunState
    data class RefreshMatrices(val matrixCount: Int) : RefreshLastRunState
    data class RefreshMatrix(val matrixState: String, val matrixId: String) : RefreshLastRunState
    object UpdatingMatrixFile : RefreshLastRunState
}
