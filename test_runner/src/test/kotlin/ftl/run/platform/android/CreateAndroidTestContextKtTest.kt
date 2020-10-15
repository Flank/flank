package ftl.run.platform.android

import com.google.common.truth.Truth.assertThat
import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.DexParser.Companion.findTestMethods
import com.linkedin.dex.parser.TestMethod
import ftl.args.AndroidArgs
import ftl.filter.TestFilter
import ftl.filter.TestFilters
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.test.util.mixedConfigYaml
import ftl.test.util.should
import ftl.util.FileReference
import ftl.util.FlankTestMethod
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateAndroidTestContextKtTest {

    @After
    fun tearDown() = unmockkAll()

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
                shards = should { size == 1 },
                ignoredTestCases = should { size == 2 }
            ),
            InstrumentationTestContext(
                app = should { local.endsWith("app-debug.apk") },
                test = should { local.endsWith("app-multiple-flaky-debug-androidTest.apk") },
                shards = should { size == 2 },
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
    fun `should filter out methods by distinct name`() {
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

    @Test
    fun `should not append all parameterized classes to list of test methods`() {
        val testInstrumentationContext = InstrumentationTestContext(
            FileReference("./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk", ""),
            FileReference("./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk", "")
        )

        mockkObject(DexParser) {
            every { findTestMethods(any()) } returns listOf(
                TestMethod("foo.bar.TestClass1#test1", emptyList()),
                TestMethod("foo.bar.TestClass1#test2", emptyList()),
                TestMethod("foo.bar.TestClass2#test1", emptyList()),
                TestMethod("foo.bar.TestClass2#test2", emptyList()),
                TestMethod("foo.bar.ParamClass#testParam", emptyList()),
            )
            mockkStatic("ftl.run.platform.android.CreateAndroidTestContextKt")
            every { testInstrumentationContext.getParametrizedClasses() } returns listOf("foo.bar.ParamClass")

            val actual = testInstrumentationContext.getFlankTestMethods(TestFilters.fromTestTargets(listOf("class foo.bar.TestClass1")))
            val expected = listOf(
                FlankTestMethod("class foo.bar.TestClass1#test1"),
                FlankTestMethod("class foo.bar.TestClass1#test2")
            )
            assertThat(actual).isEqualTo(expected)
        }
    }
}
