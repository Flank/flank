package flank.corellium.log

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class InternalTest {

    @Test
    fun groupLinesTest() {
        val lines = runBlocking {
            flowLogs()
                .groupLines()
                .toList()
        }

        // 2 * 29 statuses + 1 result
        Assert.assertEquals(59, lines.size)
    }

    @Test
    fun parseChunksTest() {
        val chunks = runBlocking {
            flowLogs()
                .groupLines()
                .parseChunks()
                .toList()
        }

        // 2 * 29 statuses + 1 result
        Assert.assertEquals(59, chunks.size)
    }

    @Test
    fun parseInstrumentStatus() {
        val statusResults = runBlocking {
            flowLogs()
                .groupLines()
                .parseChunks()
                .parseStatusResult()
                .toList()
        }

        // 29 statuses + 1 result
        Assert.assertEquals(30, statusResults.size)

        Assert.assertTrue(
            "All items except the last one must be Instrument.Status",
            statusResults.dropLast(1).all { it is Instrument.Status }
        )

        Assert.assertTrue(
            "Last item must be Instrument.Result",
            statusResults.last() is Instrument.Result
        )
    }
}

private fun flowLogs(): Flow<String> =
    Unit.javaClass.classLoader
        .getResourceAsStream("example_android_logs.log")!!
        .bufferedReader()
        .lineSequence()
        .asFlow()
