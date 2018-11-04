package ftl.json

import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.toolresults.model.Outcome
import ftl.android.AndroidCatalog
import ftl.gc.GcToolResults
import ftl.util.Billing
import ftl.util.MatrixState.FINISHED
import ftl.util.Outcome.failure
import ftl.util.Outcome.inconclusive
import ftl.util.Outcome.skipped
import ftl.util.Outcome.success
import ftl.util.webLink

// execution gcs paths aren't API accessible.
class SavedMatrix(matrix: TestMatrix) {
    val matrixId: String = matrix.testMatrixId
    var state: String = matrix.state
        private set
    val gcsPath: String = matrix.resultStorage.googleCloudStorage.gcsPath
    var webLink: String = matrix.webLink()
        private set
    var downloaded = false

    var billableVirtualMinutes: Long = 0
        private set
    var billablePhysicalMinutes: Long = 0
        private set
    var outcome: String = ""
        private set
    var outcomeAdditionalDetails: String = ""
        private set

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
        if (matrix.state != FINISHED) throw RuntimeException("Incorrect matrix state. Expected:'finished', Actual:" + matrix.state)
        billableVirtualMinutes = 0
        billablePhysicalMinutes = 0
        outcome = success
        if (matrix.testExecutions == null) return
        matrix.testExecutions.forEach {
            val step = GcToolResults.getResults(it.toolResultsStep)
            updateOutcome(step.outcome)
            if (step.testExecutionStep == null) return
            val billableMinutes = Billing.billableMinutes(step.testExecutionStep.testTiming.testProcessDuration.seconds)
            if (AndroidCatalog.isVirtualDevice(it.environment?.androidDevice)) {
                billableVirtualMinutes += billableMinutes
            } else {
                billablePhysicalMinutes += billableMinutes
            }
        }
    }

    private fun updateOutcome(stepOutcome: Outcome) {
        // 'failure' outcome gets the highest precedence,
        // so update outcome only if the current state is not failure.
        if (outcome != failure) {
            outcome = stepOutcome.summary
            when (outcome) {
                failure -> outcomeAdditionalDetails = stepOutcome.failureDetail?.keys?.joinToString(",") ?: ""
                success -> outcomeAdditionalDetails = stepOutcome.successDetail?.keys?.joinToString(",") ?: ""
                inconclusive -> outcomeAdditionalDetails = stepOutcome.inconclusiveDetail?.keys?.joinToString(",") ?: ""
                skipped -> outcomeAdditionalDetails = stepOutcome.skippedDetail?.keys?.joinToString(",") ?: ""
            }
        }
    }

    val gcsPathWithoutRootBucket get() = gcsPath.substringAfter('/')
    val gcsRootBucket get() = gcsPath.substringBefore('/')
}
