package ftl.run.platform

import ftl.args.AndroidArgs
import ftl.run.model.TestResult
import ftl.test.util.FlankTestRunner
import ftl.test.util.differentDevicesTypesPhysicalLimitYaml
import ftl.test.util.differentDevicesTypesVirtualLimitYaml
import ftl.test.util.differentDevicesTypesYaml
import ftl.test.util.mixedConfigYaml
import ftl.test.util.should
import ftl.util.FlankFatalError
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
            runAndroidTests(AndroidArgs.load(mixedConfigYaml))
        }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `should split matrices by device type when physical limit exceed`() {
        // given
        val expected = TestResult(
            should { map.size == 2 },
            listOf(
                should { size == 1 },
                should { size == 1 }
            ),
            should { size == 4 }
        )

        // when
        val actual = runBlocking {
            runAndroidTests(AndroidArgs.load(differentDevicesTypesYaml))
        }
        // then
        assertEquals(expected, actual)
    }

    @Test(expected = FlankFatalError::class)
    fun `should throw when virtual limit exceed`() {
        // given
        val expected = TestResult(
            should { map.size == 2 },
            listOf(
                should { size == 1 },
                should { size == 1 }
            ),
            should { size == 4 }
        )

        // when
        val actual = runBlocking {
            runAndroidTests(AndroidArgs.load(differentDevicesTypesVirtualLimitYaml))
        }
        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `should not split matrices by device type when in physical limit`() {
        // given
        val expected = TestResult(
            should { map.size == 1 },
            listOf(
                should { size == 1 }
            ),
            should { size == 2 }
        )

        // when
        val actual = runBlocking {
            runAndroidTests(AndroidArgs.load(differentDevicesTypesPhysicalLimitYaml))
        }

        // then
        assertEquals(expected, actual)
    }
}
