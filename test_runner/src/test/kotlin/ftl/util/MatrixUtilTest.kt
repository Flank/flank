package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
class MatrixUtilTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `testTimeoutToSeconds validInput`() {
        assertThat(timeoutToSeconds("0")).isEqualTo(0)
        assertThat(timeoutToSeconds("0s")).isEqualTo(0)
        assertThat(timeoutToSeconds("0m")).isEqualTo(0)
        assertThat(timeoutToSeconds("0h")).isEqualTo(0)
        assertThat(timeoutToSeconds("1")).isEqualTo(1)
        assertThat(timeoutToSeconds("1s")).isEqualTo(1)
        assertThat(timeoutToSeconds("1m")).isEqualTo(60)
        assertThat(timeoutToSeconds("1h")).isEqualTo(3600)
    }

    @Test(expected = NumberFormatException::class)
    fun `testTimeoutToSeconds rejectsInvalid`() {
        timeoutToSeconds("1d")
    }

    @Test
    fun `should return localResultDir if provided`() {
        val mockedArgs = mockk<IArgs>()
        every { mockedArgs.useLocalResultDir() } returns true
        every { mockedArgs.localResultDir } returns "/tmp"

        assertThat(resolveLocalRunPath(MatrixMap(emptyMap(), ""), mockedArgs)).isEqualTo("/tmp")
    }

    @Test
    fun `should newly created directory and if do not use localResultDir and runPath not exist`() {
        // given
        val matrixMap = mockk<MatrixMap>()
        every { matrixMap.runPath } returns "a/b"

        // when
        val localRunPath = resolveLocalRunPath(matrixMap, AndroidArgs.default())

        // then
        assertThat(localRunPath).isEqualTo("results/a/b")
        assertTrue(File(localRunPath).exists())
    }

    @Test
    fun `should return run path if directory already exist`() {
        val matrixMap = mockk<MatrixMap>()
        every { matrixMap.runPath } returns "/tmp"

        assertThat(resolveLocalRunPath(matrixMap, AndroidArgs.default())).isEqualTo("/tmp")
    }
}
