package ftl.run.platform

import ftl.args.AndroidArgs
import ftl.json.MatrixMap
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
        val expected = should<MatrixMap> {
            map.size == 3
        } to listOf<List<String>>(
            should { size == 1 },
            should { size == 4 },
            should { size == 5 }
        )

        // when
        val actual = runBlocking {
            runAndroidTests(AndroidArgs.load(mixedConfigYaml))
        }

        // then
        assertEquals(expected, actual)
    }
}
