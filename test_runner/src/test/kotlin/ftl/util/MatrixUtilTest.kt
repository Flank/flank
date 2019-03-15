package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.json.MatrixMap
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class MatrixUtilTest {

    @Test
    fun testTimeoutToSeconds_validInput() {
        assertThat(testTimeoutToSeconds("0")).isEqualTo(0)
        assertThat(testTimeoutToSeconds("0s")).isEqualTo(0)
        assertThat(testTimeoutToSeconds("0m")).isEqualTo(0)
        assertThat(testTimeoutToSeconds("0h")).isEqualTo(0)
        assertThat(testTimeoutToSeconds("1")).isEqualTo(1)
        assertThat(testTimeoutToSeconds("1s")).isEqualTo(1)
        assertThat(testTimeoutToSeconds("1m")).isEqualTo(60)
        assertThat(testTimeoutToSeconds("1h")).isEqualTo(60 * 60)
    }

    @Test(expected = NumberFormatException::class)
    fun testTimeoutToSeconds_rejectsInvalid() {
        testTimeoutToSeconds("1d")
    }

    @Test
    fun resolveLocalRunPath_validInput() {
        val matrixMap = mock(MatrixMap::class.java)
        `when`(matrixMap.runPath).thenReturn("a/b")
        assertThat(resolveLocalRunPath(matrixMap, AndroidArgs.default())).isEqualTo("results/b")
    }

    @Test
    fun resolveLocalRunPath_pathExists() {
        val matrixMap = mock(MatrixMap::class.java)
        `when`(matrixMap.runPath).thenReturn("/tmp")
        assertThat(resolveLocalRunPath(matrixMap, AndroidArgs.default())).isEqualTo("/tmp")
    }

    @Test
    fun validateTestShardIndex_valid() {
        val args = mock(IArgs::class.java)
        `when`(args.testShardChunks).thenReturn(listOf(listOf("")))
        validateTestShardIndex(0, args)
    }

    @Test(expected = IllegalArgumentException::class)
    fun validateTestShardIndex_invalidGreater() {
        val args = mock(IArgs::class.java)
        `when`(args.testShardChunks).thenReturn(listOf(listOf("")))
        validateTestShardIndex(99, args)
    }

    @Test(expected = IllegalArgumentException::class)
    fun validateTestShardIndex_invalidNegative() {
        val args = mock(IArgs::class.java)
        `when`(args.testShardChunks).thenReturn(listOf(listOf("")))
        validateTestShardIndex(99, args)
    }
}
