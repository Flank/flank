package ftl.corellium.result

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import java.nio.file.Paths

class LogParserTest {

    @Test
    fun `parse small logs`() = runBlocking {
        val smallResult = smallLogs.asFlow().parseLogs()
        assertEquals(3, smallResult.size)
        smallResult.filterIsInstance<TestSummary>().run {
            assertEquals(1, size)
            assertTrue(first().errors.isEmpty())
            assertEquals("1.076", first().time)
            assertEquals(-1, first().code)
        }

        smallResult.filterIsInstance<TestResult>().run {
            assertEquals(2, size)
            first().run {
                assertTrue(stack.isEmpty())
                assertEquals("com.example.test_app.InstrumentedTest", clazz)
                assertEquals(1, current)
                assertEquals("AndroidJUnitRunner", id)
                assertEquals(1, numTests)
                assertEquals("test", test)
                assertEquals(1, code)
            }
            last().run {
                assertTrue(stack.isEmpty())
                assertEquals("com.example.test_app.InstrumentedTest", clazz)
                assertEquals(1, current)
                assertEquals("AndroidJUnitRunner", id)
                assertEquals(1, numTests)
                assertEquals("test", test)
                assertEquals(0, code)
            }
        }
    }

    @Test
    fun `parse huge logs`() = runBlocking {
        val result = logs.asFlow().parseLogs()
        assertEquals(59, result.size)
        result.filterIsInstance<TestSummary>().run {
            assertEquals(1, size)
            assertEquals(11, first().errors.size)
            assertEquals("40.647", first().time)
            assertEquals(-1, first().code)
        }
        result.filterIsInstance<TestResult>().run {
            assertEquals(58, size)
            first().run {
                assertTrue(stack.isEmpty())
                assertEquals("com.example.test_app.InstrumentedTest", clazz)
                assertEquals(1, current)
                assertEquals("AndroidJUnitRunner", id)
                assertEquals(29, numTests)
                assertEquals("ignoredTestWithIgnore", test)
                assertEquals(1, code)
            }
            last().run {
                assertEquals(38, stack.size)
                assertEquals("java.lang.AssertionError", stack.first())
                assertEquals("at android.app.Instrumentation\$InstrumentationThread.run(Instrumentation.java:2205)", stack.last())
                assertEquals("com.example.test_app.similar.SimilarNameTest1", clazz)
                assertEquals(29, current)
                assertEquals("AndroidJUnitRunner", id)
                assertEquals(29, numTests)
                assertEquals("test2", test)
                assertEquals(-2, code)
            }
        }
    }

    @Ignore("TBD")
    @Test
    fun `parse huge logs -- parser 2`() = runBlocking {
        val result = logs.asFlow().parseLogs2()
        with(result) {
            assertEquals(58, size)
            first().run {
                assertTrue(stream.isEmpty())
                assertEquals("com.example.test_app.InstrumentedTest", clazz)
                assertEquals(1, current)
                assertEquals("AndroidJUnitRunner", id)
                assertEquals(29, numTests)
                assertEquals("ignoredTestWithIgnore", test)
                assertEquals(1, code)
            }
            last().run {
                assertEquals(38, stream.size)
                assertEquals("java.lang.AssertionError", stream.first())
                assertEquals("at android.app.Instrumentation\$InstrumentationThread.run(Instrumentation.java:2205)", stream.last())
                assertEquals("com.example.test_app.similar.SimilarNameTest1", clazz)
                assertEquals(29, current)
                assertEquals("AndroidJUnitRunner", id)
                assertEquals(29, numTests)
                assertEquals("test2", test)
                assertEquals(-2, code)
            }
        }
    }
}

private val logs = Paths.get("./src/test/kotlin/ftl/corellium/result/test_logs.log").toFile().readLines()
private val smallLogs = Paths.get("./src/test/kotlin/ftl/corellium/result/small_logs.log").toFile().readLines()
