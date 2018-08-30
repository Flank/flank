package ftl.json

import com.google.api.services.testing.model.GoogleCloudStorage
import com.google.api.services.testing.model.ResultStorage
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.ToolResultsStep
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import ftl.util.MatrixState.FINISHED
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class SavedMatrixTest {

    // use -1 step id to get a failure outcome from the mock server
    private fun createStepExecution(stepId: Int): TestExecution {
        val toolResultsStep = ToolResultsStep()
        toolResultsStep.projectId = "1"
        toolResultsStep.historyId = "2"
        toolResultsStep.executionId = "3"
        toolResultsStep.stepId = stepId.toString()

        val testExecution = TestExecution()
        testExecution.toolResultsStep = toolResultsStep
        return testExecution
    }

    private fun createResultsStorage(): ResultStorage {
        val googleCloudStorage = GoogleCloudStorage()
        googleCloudStorage.gcsPath = "path"

        val resultsStorage = ResultStorage()
        resultsStorage.googleCloudStorage = googleCloudStorage
        return resultsStorage
    }

    @Test
    fun savedMatrixOutcome() {
        // Verify that if we have two executions: failure then success
        // the SavedMatrix outcome is correctly recorded as failure
        val testExecutions = listOf(
                createStepExecution(-1),
                createStepExecution(1))

        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions

        val savedMatrix = SavedMatrix(testMatrix)
        assert(savedMatrix.outcome, "failure")
    }
}
