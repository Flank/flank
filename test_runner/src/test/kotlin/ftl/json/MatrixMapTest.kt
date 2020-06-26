package ftl.json

import com.google.api.services.testing.model.TestMatrix
import com.google.common.truth.Truth.assertThat
import ftl.json.SavedMatrixTest.Companion.createResultsStorage
import ftl.json.SavedMatrixTest.Companion.createStepExecution
import ftl.test.util.FlankTestRunner
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

        assertThat(matrixMap.allSuccessful()).isTrue()
        assertThat(matrixMap.runPath).isNotNull()
        assertThat(matrixMap.map).isNotNull()
    }

    private fun matrixForExecution(executionId: Int): SavedMatrix {
        return SavedMatrix(
            matrix = TestMatrix()
                .setResultStorage(createResultsStorage())
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

        assertThat(matrixMap.allSuccessful()).isFalse()
    }
}
