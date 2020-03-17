package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.json.MatrixMap
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class MatrixUtilTest {

    @Test
    fun `testTimeoutToSeconds validInput`() {
        assertThat(timeoutToSeconds("0")).isEqualTo(0)
        assertThat(timeoutToSeconds("0s")).isEqualTo(0)
        assertThat(timeoutToSeconds("0m")).isEqualTo(0)
        assertThat(timeoutToSeconds("0h")).isEqualTo(0)
        assertThat(timeoutToSeconds("1")).isEqualTo(1)
        assertThat(timeoutToSeconds("1s")).isEqualTo(1)
        assertThat(timeoutToSeconds("1m")).isEqualTo(60)
        assertThat(timeoutToSeconds("1h")).isEqualTo(60 * 60)
    }

    @Test(expected = NumberFormatException::class)
    fun `testTimeoutToSeconds rejectsInvalid`() {
        timeoutToSeconds("1d")
    }

    @Test
    fun `resolveLocalRunPath validInput`() {
        val matrixMap = mock(MatrixMap::class.java)
        `when`(matrixMap.runPath).thenReturn("a/b")
        assertThat(resolveLocalRunPath(matrixMap, AndroidArgs.default())).isEqualTo("results/b")
    }

    @Test
    fun `resolveLocalRunPath pathExists`() {
        val matrixMap = mock(MatrixMap::class.java)
        `when`(matrixMap.runPath).thenReturn("/tmp")
        assertThat(resolveLocalRunPath(matrixMap, AndroidArgs.default())).isEqualTo("/tmp")
    }
}
