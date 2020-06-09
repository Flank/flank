package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.test.util.mixedConfigYaml
import ftl.test.util.should
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateAndroidTestContextKtTest {

    @Test
    fun `create AndroidTestConfig for mixed tests`() {
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
                    should { size == 3 },
                    should { size == 3 }
                )
            )
        )

        // when
        val actual: List<AndroidTestContext> = runBlocking {
            AndroidArgs.load(mixedConfigYaml).createAndroidTestContexts()
        }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `should contains parametrized test`() {
        // given
        val expected = "class com.example.test_app.ParameterizedTest"

        // when
        val actual = runBlocking {
            AndroidArgs.load(mixedConfigYaml).createAndroidTestContexts()
        }.map { it as? InstrumentationTestContext }.filterNotNull().flatMap { it.shards.flatten() }

        // then
        Assert.assertTrue(actual.contains(expected))
    }
}
