package ftl.util

import com.google.api.services.toolresults.model.AndroidTest
import com.google.api.services.toolresults.model.Duration
import com.google.api.services.toolresults.model.Specification
import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.json.MatrixMap
import ftl.json.testTimeoutSeconds
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

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
        assertThat(timeoutToSeconds("1h")).isEqualTo(60 * 60)
    }

    @Test(expected = NumberFormatException::class)
    fun `testTimeoutToSeconds rejectsInvalid`() {
        timeoutToSeconds("1d")
    }

    @Test
    fun `resolveLocalRunPath validInput`() {
        val matrixMap = mockk<MatrixMap>()
        every { matrixMap.runPath } returns "a/b"

        assertThat(resolveLocalRunPath(matrixMap, AndroidArgs.default())).isEqualTo("results/b")
    }

    @Test
    fun `resolveLocalRunPath pathExists`() {
        val matrixMap = mockk<MatrixMap>()
        every { matrixMap.runPath } returns "/tmp"

        assertThat(resolveLocalRunPath(matrixMap, AndroidArgs.default())).isEqualTo("/tmp")
    }

    @Test
    fun `specification should contains test timeout in androidTest`() {
        val spec = Specification()
        spec.androidTest = AndroidTest()
        spec.androidTest.testTimeout = Duration()
        spec.androidTest.testTimeout.seconds = 100
        Assert.assertEquals(100L, spec.testTimeoutSeconds())
    }

    @Test
    fun `specification should contains test timeout in iosTest`() {
        val spec = Specification()
        val test = AndroidTest()
        test.testTimeout = Duration()
        test.testTimeout .seconds = 100
        spec["iosTest"] = test
        Assert.assertEquals(100L, spec.testTimeoutSeconds())
    }
}
