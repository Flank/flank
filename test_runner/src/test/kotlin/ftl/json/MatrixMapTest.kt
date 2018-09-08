package ftl.json

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import ftl.util.Outcome
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class MatrixMapTest {

    @Test
    fun matrixMap_successful() {
        val successMatrix = mock(SavedMatrix::class.java)
        `when`(successMatrix.outcome).thenReturn(Outcome.success)
        val matrixMap = MatrixMap(mutableMapOf("" to successMatrix, "" to successMatrix), "")

        assertThat(matrixMap.allSuccessful()).isTrue()
        assertThat(matrixMap.runPath).isNotNull()
        assertThat(matrixMap.map).isNotNull()
    }

    @Test
    fun matrixMap_failure() {
        val successMatrix = mock(SavedMatrix::class.java)
        `when`(successMatrix.outcome).thenReturn(Outcome.success)
        val failedMatrix = mock(SavedMatrix::class.java)
        `when`(successMatrix.outcome).thenReturn(Outcome.failure)
        val matrixMap = MatrixMap(mutableMapOf("" to failedMatrix, "" to successMatrix), "")

        assertThat(matrixMap.allSuccessful()).isFalse()
    }
}
