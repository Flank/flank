package ftl.util

import com.google.api.services.testing.model.TestMatrix
import com.google.common.truth.Truth.assertThat
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.json.SavedMatrixTest
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
        // Given
        var savedMatrixTest = SavedMatrixTest()
        val testExecutions = listOf(
            savedMatrixTest.createStepExecution(1, "Success"),
            savedMatrixTest.createStepExecution(-1, "Failed")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = savedMatrixTest.createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("finishedMatrix" to finishedMatrix), "MockPath")
        // When
        var exitCode = Utils.getExitCode(matrixMap)
        // Then
        assertThat(exitCode).isEqualTo(1)
    }

    @Test
    fun testExitCodeForSuccess() {
        // Given
        var savedMatrixTest = SavedMatrixTest()
        val testExecutions = listOf(
            savedMatrixTest.createStepExecution(1, "Success")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = savedMatrixTest.createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath")
        // When
        var exitCode = Utils.getExitCode(matrixMap)
        // Then
        assertThat(exitCode).isEqualTo(0)
    }

    @Test
    fun testExitCodeForInconclusive() {
        // Given
        var savedMatrixTest = SavedMatrixTest()
        val testExecutions = listOf(
            savedMatrixTest.createStepExecution(-2, "Inconclusive")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.FINISHED
        testMatrix.resultStorage = savedMatrixTest.createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val finishedMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("" to finishedMatrix), "MockPath")
        // When
        var exitCode = Utils.getExitCode(matrixMap)
        // Then
        assertThat(exitCode).isEqualTo(2)
    }

    @Test
    fun testExitCodeForError() {
        // Given
        var savedMatrixTest = SavedMatrixTest()
        val testExecutions = listOf(
            savedMatrixTest.createStepExecution(-2, "Inconclusive"),
            savedMatrixTest.createStepExecution(-3, "Skipped")
        )
        val testMatrix = TestMatrix()
        testMatrix.testMatrixId = "123"
        testMatrix.state = MatrixState.ERROR
        testMatrix.resultStorage = savedMatrixTest.createResultsStorage()
        testMatrix.testExecutions = testExecutions
        val errorMatrix = SavedMatrix(testMatrix)
        val matrixMap = MatrixMap(mutableMapOf("errorMatrix" to errorMatrix), "MockPath")
        // When
        var exitCode = Utils.getExitCode(matrixMap)
        // Then
        assertThat(exitCode).isEqualTo(2)
    }
}
