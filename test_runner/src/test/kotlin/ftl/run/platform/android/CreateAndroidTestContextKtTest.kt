package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.test.util.TestHelper
import ftl.test.util.should
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateAndroidTestContextKtTest {

    private val mixedYamlFile = TestHelper.getPath("src/test/kotlin/ftl/fixtures/test_app_cases/flank-multiple-mixed.yml")

    @Test
    fun `create AndroidTestConfig for robo and instrumentation tests`() {
        // given
        val expected = listOf(
            RoboTestContext(
                app = should { local.endsWith("app-debug.apk") },
                roboScript = should { local.endsWith("MainActivity_robo_script.json") }
            ),
            InstrumentationTestContext(
                app = should { local.endsWith("app-debug.apk") },
                test = should { local.endsWith("app-single-success-debug-androidTest.apk") },
                shards = listOf(
                    should { size == 1 }
                )
            ),
            InstrumentationTestContext(
                app = should { local.endsWith("app-debug.apk") },
                test = should { local.endsWith("app-multiple-flaky-debug-androidTest.apk") },
                shards = listOf(
                    should { size == 2 },
                    should { size == 3 }
                )
            )
        )

        // when
        val actual: List<AndroidTestContext> = runBlocking {
            AndroidArgs.load(mixedYamlFile).createAndroidTestContexts()
        }

        // then
        assertEquals(expected, actual)
    }
}
