package ftl.run.common

import ftl.args.IArgs
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.run.model.RunningDevices
import ftl.util.MatrixState
import ftl.util.StopWatch
import ftl.util.StopWatchMatrix
import ftl.util.completed
import ftl.util.webLink
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/** Synchronously poll all matrix ids until they complete. Returns true if test run passed. **/
internal suspend fun pollMatrices(matrices: MatrixMap, args: IArgs) = coroutineScope {
    println("PollMatrices")
    val poll = matrices.map.values.filter {
        MatrixState.inProgress(it.state)
    }

    val stopwatch = StopWatch().start()
    poll.forEach {
        val matrixId = it.matrixId
        pollMatrix(matrixId, stopwatch, args, matrices)
    }
    println()

    updateMatrixFile(matrices, args)
}

// Used for when the matrix has exactly one test. Polls for detailed progress
//
// Port of MonitorTestExecutionProgress
// gcloud-cli/googlecloudsdk/api_lib/firebase/test/matrix_ops.py
private suspend fun pollMatrix(matrixId: String, stopwatch: StopWatch, args: IArgs, matrices: MatrixMap) = coroutineScope {
    var refreshedMatrix = GcTestMatrix.refresh(matrixId, args.project)
    val watch = StopWatchMatrix(stopwatch, matrixId)
    val runningDevices = RunningDevices(stopwatch, refreshedMatrix.testExecutions)
    var printWebLink = true

    while (true) {
        val webLink = refreshedMatrix.webLink()
        if (printWebLink && webLink.isNotBlank()) {
            printWebLink = false
            println(webLink)
        }

        if (matrices.map[matrixId]?.update(refreshedMatrix) == true) updateMatrixFile(
            matrices,
            args
        )

        runningDevices.allRunning().forEach { nextDevice ->
            nextDevice.poll(refreshedMatrix)
        }

        // Matrix has 0 or more devices (test executions)
        // Verify all executions are complete & the matrix itself is marked as complete.
        if (runningDevices.allComplete() && refreshedMatrix.completed()) {
            break
        }

        // GetTestMatrix is not designed to handle many requests per second.
        // Sleep to avoid overloading the system.
        delay(5_000)
        refreshedMatrix = GcTestMatrix.refresh(matrixId, args.project)
    }

    // Print final matrix state with timestamp. May be many minutes after the 'Done.' progress message.
    watch.puts(refreshedMatrix.state)
}
