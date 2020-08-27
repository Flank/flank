package ftl.run.platform.android

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.DexParser.Companion.findTestMethods
import com.linkedin.dex.parser.TestMethod
import ftl.args.AndroidArgs
import ftl.filter.TestFilter
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.test.util.mixedConfigYaml
import ftl.test.util.should
import ftl.util.FileReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
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
                ),
                ignoredTestCases = should { size == 2 }
            ),
            InstrumentationTestContext(
                app = should { local.endsWith("app-debug.apk") },
                test = should { local.endsWith("app-multiple-flaky-debug-androidTest.apk") },
                shards = listOf(
                    should { size == 4 && testsList.contains("class com.example.test_app.ParameterizedTest") },
                    should { size == 5 }
                ),
                ignoredTestCases = should { size == 4 }
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
    fun `Should filter out methods by distinct name`() {
        // given
        val testInstrumentationContext = InstrumentationTestContext(
            FileReference("./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk", ""),
            FileReference("./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk", "")
        )
        var actual = 0

        // when
        mockkObject(DexParser) {
            every { findTestMethods(any()) } returns listOf(
                TestMethod("testMethod", listOf(mockk(relaxed = true))),
                TestMethod("testMethod", listOf(mockk(relaxed = true))),
                TestMethod("testMethod", listOf()),
                TestMethod("testMethod2", listOf(mockk(relaxed = true))),
                TestMethod("testMethod2", listOf(mockk(relaxed = true))),
                TestMethod("testMethod2", listOf()),
            )

            actual = testInstrumentationContext
                .getFlankTestMethods(TestFilter("test", { true }))
                .size
        }

        // given
        assertEquals(actual, 2)
    }
}
