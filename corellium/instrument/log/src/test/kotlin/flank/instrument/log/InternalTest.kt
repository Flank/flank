package flank.instrument.log

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class InternalTest {

    @Test
    fun groupLinesTest() {
        val lines = runBlocking {
            flowLogs(LOG)
                .groupLines()
                .toList()
        }
        lines.forEach {
            println()
            it.forEach {
                println(it)
            }
        }

        // 2 * 29 statuses + 1 result
        Assert.assertEquals(59, lines.size)
    }

    @Test
    fun parseChunksTest() {
        val chunks = runBlocking {
            flowLogs(LOG)
                .groupLines()
                .parseChunks()
                .toList()
        }

        chunks.forEach { chunk ->
            println()
            chunk.map.forEach { (key, value) ->
                println("$key: $value")
            }
            println("timestamp: ${chunk.timestamp}")
            println("type: ${chunk.type}")
            println("code: ${chunk.code}")
        }

        // 2 * 29 statuses + 1 result
        Assert.assertEquals(59, chunks.size)
    }

    @Test
    fun parseInstrumentStatus() {
        val statusResults = runBlocking {
            flowLogs(LOG)
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
