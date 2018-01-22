package ftl.json

import com.google.testing.model.TestMatrix
import ftl.util.MatrixState.FINISHED
import ftl.gc.GcToolResults
import ftl.util.Billing
import ftl.util.firstToolResults
import ftl.util.webLink

// Note: 1 matrix = 1 execution is assumed.
// execution gcs paths aren't API accessible.
class SavedMatrix(matrix: TestMatrix) {
    val matrixId: String = matrix.testMatrixId
    var state: String = matrix.state
    val gcsPath: String = matrix.resultStorage.googleCloudStorage.gcsPath
    var webLink: String = matrix.webLink()
    var downloaded = false

    var billableMinutes: Long = 0
    var testDurationSeconds: Long = 0
    var runDurationSeconds: Long = 0
    var outcome: String = ""

    /** return true if the content changed **/
    fun update(matrix: TestMatrix): Boolean {
        val newState = matrix.state
        val newLink = matrix.webLink()
        val changedState = state != newState
        val changedLink = webLink != newLink

        if (changedState) {
            this.state = newState
            if (this.state == FINISHED) finished(matrix)
        }

        if (changedLink) {
            this.webLink = newLink
        }

        return changedState || changedLink
    }

    private fun finished(matrix: TestMatrix) {
        val toolResultsStep = matrix.firstToolResults()
        if (toolResultsStep != null) {
           val step = GcToolResults.getResults(toolResultsStep)
            testDurationSeconds = step.testExecutionStep.testTiming.testProcessDuration.seconds
            runDurationSeconds = step.runDuration.seconds
            billableMinutes = Billing.billableMinutes(testDurationSeconds)
            outcome = step.outcome.summary
        }
    }

    val gcsPathWithoutRootBucket get() = gcsPath.substringAfter('/')
    val gcsRootBucket get() = gcsPath.substringBefore('/')
}
