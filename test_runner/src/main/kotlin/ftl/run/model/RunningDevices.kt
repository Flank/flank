package ftl.run.model

import com.google.api.services.testing.model.Environment
import com.google.api.services.testing.model.TestDetails
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import ftl.util.MatrixState
import ftl.util.StopWatch
import ftl.util.StopWatchMatrix

class RunningDevice(private val stopwatch: StopWatch, val id: String) {
    var lastState = ""
    var lastError = ""
    var progress = listOf<String>()
    var lastProgressLen = 0
    val details: TestDetails? = null
    var complete = false

    private fun device(testExecution: TestExecution): String {
        val env: Environment? = testExecution.environment
        val device = env?.androidDevice?.androidModelId ?: env?.iosDevice?.iosModelId
        val deviceVersion = env?.androidDevice?.androidVersionId ?: env?.iosDevice?.iosVersionId
        return "$device-$deviceVersion"
    }

    fun poll(matrix: TestMatrix) {
        val testExecution = matrix.testExecutions.single { it.id == id }
        val watch = StopWatchMatrix(stopwatch, "${testExecution.matrixId} ${device(testExecution)}")
        val details: TestDetails? = testExecution.testDetails

        if (details != null) {
            // Error message is never reset. Track last error to only print new messages.
            val errorMessage = details.errorMessage
            if (
                    errorMessage != null &&
                    errorMessage != lastError
            ) {
                // Note: After an error (infrastructure failure), FTL will retry 3x
                lastError = errorMessage
                watch.puts("Error: $lastError")
            }
            progress = details.progressMessages ?: progress
        }

        // num-flaky-test-attempts restarts progress array at size 1
        if (lastProgressLen > progress.size) {
            lastProgressLen = 0
        }

        // Progress contains all messages. only print new updates
        for (msg in progress.listIterator(lastProgressLen)) {
            watch.puts(msg)
        }

        lastProgressLen = progress.size

        if (testExecution.state != lastState) {
            lastState = testExecution.state
            watch.puts(lastState)
        }

        if (MatrixState.completed(testExecution.state)) {
            complete = true
        }
    }
}

class RunningDevices(stopwatch: StopWatch, testExecutions: List<TestExecution>) {
    private val devices = testExecutions.map { RunningDevice(stopwatch, it.id) }

    fun next(): RunningDevice? = devices.firstOrNull { it.complete.not() }

    fun allRunning(): List<RunningDevice> = devices.filter { it.complete.not() }

    fun allComplete(): Boolean = devices.all { it.complete }
}
