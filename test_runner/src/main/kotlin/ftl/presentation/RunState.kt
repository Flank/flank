package ftl.presentation

import com.google.common.annotations.VisibleForTesting
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

interface RunState

@VisibleForTesting
internal val runSharedFlow by lazy {
    MutableSharedFlow<RunState>(extraBufferCapacity = 1)
}

internal fun Output.runBlockingWithObservingRunState(block: suspend () -> Unit) {
    runBlocking {
        val runStateCollectJob = launch {
            runSharedFlow.collect { runState -> runState.out() }
        }
        block()
        runStateCollectJob.cancel()
    }
}

internal fun RunState.publish() {
    runSharedFlow.tryEmit(this)
}
