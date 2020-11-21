package ftl.run.platform

import ftl.args.AndroidArgs
import ftl.gc.GcAndroidTestMatrix
import ftl.run.model.TestResult
import ftl.test.util.FlankTestRunner
import ftl.test.util.mixedConfigYaml
import ftl.test.util.should
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
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
                should { size == 4 },
                should { size == 5 }
            ),
            should { size == 6 }
        )

        // when
        val actual = runBlocking {
            AndroidArgs.load(mixedConfigYaml).runAndroidTests()
        }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `should add additional index if repeatTests set`() {
        val androidArgs = AndroidArgs.load(mixedConfigYaml)
        mockkObject(GcAndroidTestMatrix, androidArgs) {

            every { androidArgs.resultsDir } returns "test_dir"
            every { androidArgs.resultsBucket } returns "test_bucket"
            every { androidArgs.repeatTests } returns 2

            runBlocking {
                androidArgs.runAndroidTests()
            }

            verify {
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_0/", any(), any(), any(), any(), any())
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_0_1/", any(), any(), any(), any(), any())
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_1/", any(), any(), any(), any(), any())
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_1_1/", any(), any(), any(), any(), any())
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_2/", any(), any(), any(), any(), any())
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_2_1/", any(), any(), any(), any(), any())
            }

            verify(inverse = true) {
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_0_2/", any(), any(), any(), any(), any())
            }
        }
    }

    @Test
    fun `shouldn't add additional index if repeatTests not set`() {
        val androidArgs = AndroidArgs.load(mixedConfigYaml)
        mockkObject(GcAndroidTestMatrix, androidArgs) {

            every { androidArgs.resultsDir } returns "test_dir"
            every { androidArgs.resultsBucket } returns "test_bucket"

            runBlocking {
                androidArgs.runAndroidTests()
            }

            verify(inverse = true) {
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_0_1/", any(), any(), any(), any(), any())
            }

            verify {
                GcAndroidTestMatrix.build(any(), any(), "test_dir/matrix_0/", any(), any(), any(), any(), any())
            }
        }
    }
}
