package ftl.json

import com.google.common.truth.Truth.assertThat
import ftl.api.TestMatrix
import ftl.json.SavedMatrixTest.Companion.createResultsStorage
import ftl.json.SavedMatrixTest.Companion.createStepExecution
import ftl.json.SavedMatrixTest.Companion.testMatrix
import ftl.run.exception.MatrixValidationError
import ftl.test.util.FlankTestRunner
import ftl.test.util.assertThrowsWithMessage
import ftl.util.MatrixState
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class MatrixMapTest {

    @Test
    fun `matrixMap successful`() {
        val successMatrix1 = matrixForExecution(0)
        val successMatrix2 = matrixForExecution(0)
        val matrixMap = MatrixMap(mutableMapOf("a" to successMatrix1, "b" to successMatrix2), "")

        assertThat(matrixMap.isAllSuccessful()).isTrue()
        assertThat(matrixMap.runPath).isNotNull()
        assertThat(matrixMap.map).isNotNull()
    }

    private fun matrixForExecution(executionId: Int): TestMatrix.Data {
        return createAndUpdateMatrix(
            testMatrix()
                .setResultStorage(
                    createResultsStorage().apply {
                        toolResultsExecution.executionId = executionId.toString()
                    }
                )
                .setState(MatrixState.FINISHED)
                .setTestMatrixId("123")
                .setTestExecutions(
                    listOf(
                        createStepExecution(executionId, "")
                    )
                )
        )
    }

    @Test
    fun `matrixMap failure`() {
        val successMatrix = matrixForExecution(0) // 0 = success
        val failureMatrix = matrixForExecution(-1) // -1 = failure
        val matrixMap = MatrixMap(mutableMapOf("a" to failureMatrix, "b" to successMatrix), "")

        assertThat(matrixMap.isAllSuccessful()).isFalse()
    }

    @Test
    fun `invalid matrix should contain a specific error message`() {
        val testMatrix = testMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.INVALID

        val savedMatrix = createAndUpdateMatrix(testMatrix)

        val expectedErrorMessage = "Matrix: [${testMatrix.testMatrixId}] failed: Unknown error"
        assertThrowsWithMessage(MatrixValidationError::class, expectedErrorMessage) {
            MatrixMap(mapOf(savedMatrix.matrixId to savedMatrix), "").validate()
        }
    }
}
