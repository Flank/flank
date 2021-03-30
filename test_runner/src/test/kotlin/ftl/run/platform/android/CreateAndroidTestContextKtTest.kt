package ftl.run.platform.android

import com.google.common.truth.Truth.assertThat
import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.DexParser.Companion.findTestMethods
import com.linkedin.dex.parser.TestMethod
import ftl.args.AndroidArgs
import ftl.filter.TestFilter
import ftl.filter.TestFilters
import ftl.run.common.prettyPrint
import ftl.run.model.AndroidTestContext
import ftl.run.model.AndroidTestShards
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Paths

class CreateAndroidTestContextKtTest {

    @get:Rule
    val root = TemporaryFolder()

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

            val actual =
                testInstrumentationContext.getFlankTestMethods(TestFilters.fromTestTargets(listOf("class foo.bar.TestClass1")))
            val expected = listOf(
                FlankTestMethod("class foo.bar.TestClass1#test1"),
                FlankTestMethod("class foo.bar.TestClass1#test2")
            )
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `should create contexts based of user provided sharding`() {
        val customSharding =
            mapOf(
                "matrix-0" to AndroidTestShards(
                    app = "./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
                    test = "./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk",
                    shards = mapOf(
                        "shard-0" to listOf(
                            "class com.example.test_app.InstrumentedTest#test"
                        )
                    ),
                    junitIgnored = listOf(
                        "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                        "class com.example.test_app.InstrumentedTest#ignoredTestWithSuppress"
                    )
                ),
                "matrix-1" to AndroidTestShards(
                    app = "./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
                    test = "./src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-flaky-debug-androidTest.apk",
                    shards = mapOf(
                        "shard-0" to listOf(
                            "class com.example.test_app.InstrumentedTest#test1",
                            "class com.example.test_app.InstrumentedTest#test2"
                        ),
                        "shard-1" to listOf(
                            "class com.example.test_app.ParameterizedTest",
                            "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner",
                            "class com.example.test_app.InstrumentedTest#test0"
                        ),
                        "shard-2" to listOf(
                            "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
                            "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized"
                        ),
                        "shard-3" to listOf(
                            "class com.example.test_app.bar.BarInstrumentedTest#testBar",
                            "class com.example.test_app.foo.FooInstrumentedTest#testFoo"
                        )
                    ),
                    junitIgnored = listOf(
                        "class com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
                        "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                        "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
                        "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
                    )
                ),
                "matrix-2" to AndroidTestShards(
                    app = "./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
                    test = "./src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk",
                    shards = mapOf(
                        "shard-0" to listOf(
                            "class com.example.test_app.InstrumentedTest#test1",
                            "class com.example.test_app.InstrumentedTest#test2"
                        ),
                        "shard-1" to listOf(
                            "class com.example.test_app.ParameterizedTest",
                            "class com.example.test_app.parametrized.EspressoParametrizedMethodTestJUnitParamsRunner"
                        ),
                        "shard-2" to listOf(
                            "class com.example.test_app.InstrumentedTest#test0",
                            "class com.example.test_app.bar.BarInstrumentedTest#testBar",
                            "class com.example.test_app.foo.FooInstrumentedTest#testFoo",
                            "class com.example.test_app.parametrized.EspressoParametrizedClassParameterizedNamed",
                            "class com.example.test_app.parametrized.EspressoParametrizedClassTestParameterized"
                        )
                    ),
                    junitIgnored = listOf(
                        "class com.example.test_app.InstrumentedTest#ignoredTestWitSuppress",
                        "class com.example.test_app.InstrumentedTest#ignoredTestWithIgnore",
                        "class com.example.test_app.bar.BarInstrumentedTest#ignoredTestBar",
                        "class com.example.test_app.foo.FooInstrumentedTest#ignoredTestFoo"
                    )
                )
            )

        val customShardingFile = root.newFile("custom_sharding.json").also {
            it.writeText(prettyPrint.toJson(customSharding))
        }

        val actual: List<AndroidTestContext> = runBlocking {
            AndroidArgs.load(
                Paths.get("./src/test/kotlin/ftl/fixtures/test_app_cases/flank-multiple-mixed-with-additional-apks.yml")
            ).run {
                copy(
                    commonArgs = commonArgs.copy(
                        shardingJson = customShardingFile.absolutePath
                    )
                ).createAndroidTestContexts()
            }
        }

        assertEquals(4, actual.size)

        val roboContexts = actual.filterIsInstance<RoboTestContext>()
        assertEquals(1, roboContexts.size)

        val instrumentationContexts = actual.filterIsInstance<InstrumentationTestContext>()
        assertEquals(3, instrumentationContexts.size)

        customSharding.values.forEach { customShards ->
            val context = instrumentationContexts.filter {
                it.app.local.contains(customShards.app.drop(1)) &&
                    it.test.local.contains(customShards.test.drop(1))
            }.run {
                assertEquals(1, size)
                get(0)
            }

            assertEquals(customShards.junitIgnored, context.ignoredTestCases)
            assertTrue(customShards.shards.values.containsAll(context.shards.map { it.testMethodNames }))
        }
    }
}
