package ftl.corellium.result

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        assertEquals(2, smallResult.filterIsInstance<TestResult>().size)
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
        assertEquals(58, result.filterIsInstance<TestResult>().size)
    }
}

private val logs = Paths.get("./src/test/kotlin/ftl/corellium/result/test_logs.log").toFile().readLines()
private val smallLogs = Paths.get("./src/test/kotlin/ftl/corellium/result/small_logs.log").toFile().readLines()
