package ftl.util

import com.google.api.services.testing.model.TestMatrix
import com.google.common.truth.Truth.assertThat
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.json.SavedMatrixTest.Companion.createResultsStorage
import ftl.json.SavedMatrixTest.Companion.createStepExecution
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class UtilsTest {

    @Test(expected = RuntimeException::class)
    fun readTextResource_errors() {
        Utils.readTextResource("does not exist")
    }

    @Test
    fun readTextResource_succeeds() {
        assertThat(Utils.readTextResource("version.txt")).isNotNull()
    }

    @Test
    fun testExitCodeForFailed() {
        val testExecutions = listOf(
            createStepExecution(1, "Success"),
            createStepExecution(-1, "Failed")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("finishedMatrix" to finishedMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(1)
    }

    @Test
    fun testExitCodeForSuccess() {
        val testExecutions = listOf(
            createStepExecution(1, "Success")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(0)
    }

    @Test
    fun testExitCodeForInconclusive() { // inconclusive is treated as a failure
        val testExecutions = listOf(
            createStepExecution(-2, "Inconclusive")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(1)
    }

    @Test
    fun testExitCodeForError() {
        val testExecutions = listOf(
            createStepExecution(-2, "Inconclusive"),
            createStepExecution(-3, "Skipped")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.ERROR
        testMatrix.resultStorage = createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val errorMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("errorMatrix" to errorMatrix), "MockPath")
        assertThat(matrixMap.exitCode()).isEqualTo(2)
    }
}
