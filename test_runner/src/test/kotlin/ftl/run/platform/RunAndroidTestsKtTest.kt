package ftl.run.platform

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.client.google.run.android.createGcsPath
import ftl.run.model.TestResult
import ftl.test.util.FlankTestRunner
import ftl.test.util.mixedConfigYaml
import ftl.test.util.should
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class RunAndroidTestsKtTest {

    @Test
    fun `run android tests for mixed contexts`() {
        // given
        val expected = TestResult(
            should { map.size == 3 },
            listOf(
                should { size == 1 },
                should { size == 1 },
                should { size == 2 }
            ),
            should { size == 4 }
        )

        // when
        val actual = runBlocking {
            AndroidArgs.load(mixedConfigYaml).runAndroidTests()
        }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `should add additional index if run count not 0`() {
        // given
        val testDir = "test_dir"

        // when
        val actual0 = testDir.createGcsPath(contextIndex = 0, runIndex = 2)
        val actual1 = testDir.createGcsPath(contextIndex = 1, runIndex = 1)
        val actual2 = testDir.createGcsPath(contextIndex = 2, runIndex = 3)

        // then
        assertThat(actual0).isEqualTo("$testDir/matrix_0_2/")
        assertThat(actual1).isEqualTo("$testDir/matrix_1_1/")
        assertThat(actual2).isEqualTo("$testDir/matrix_2_3/")
    }

    @Test
    fun `shouldn't add additional index if run count is 0`() {
        // given
        val testDir = "test_dir"

        // when
        val actual0 = testDir.createGcsPath(contextIndex = 0, runIndex = 0)
        val actual1 = testDir.createGcsPath(contextIndex = 1, runIndex = 0)
        val actual2 = testDir.createGcsPath(contextIndex = 2, runIndex = 0)

        // then
        assertThat(actual0).isEqualTo("$testDir/matrix_0/")
        assertThat(actual1).isEqualTo("$testDir/matrix_1/")
        assertThat(actual2).isEqualTo("$testDir/matrix_2/")
    }
}
