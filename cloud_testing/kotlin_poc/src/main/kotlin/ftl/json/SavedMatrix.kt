package ftl.json

import com.google.api.services.testing.model.TestMatrix
import ftl.android.AndroidCatalog
import ftl.gc.GcToolResults
import ftl.util.Billing
import ftl.util.MatrixState.FINISHED
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

    var billableVirtualMinutes: Long = 0
    var billablePhysicalMinutes: Long = 0
    var outcome: String = ""

    init {
        if (this.state == FINISHED) finished(matrix)
    }

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
        billableVirtualMinutes = 0
        billablePhysicalMinutes = 0
        matrix.testExecutions.forEach {
            val step = GcToolResults.getResults(it.toolResultsStep)
            val billableMinutes = Billing.billableMinutes(step.testExecutionStep.testTiming.testProcessDuration.seconds)
            if (AndroidCatalog.isVirtualDevice(it.environment?.androidDevice)) {
                billableVirtualMinutes += billableMinutes
            } else {
                billablePhysicalMinutes += billableMinutes
            }
            outcome = step.outcome.summary
        }
    }

    val gcsPathWithoutRootBucket get() = gcsPath.substringAfter('/')
    val gcsRootBucket get() = gcsPath.substringBefore('/')
}
