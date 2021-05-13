package flank.corellium.client.parser

import flank.corellium.client.console.normalizeLines
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class FunctionsKtTest {

    @Test
    fun normalizeLinesTest() {
        val input = listOf(
            "l",
            "in",
            "e1\nline2\nline3\nli",
            "ne4\nl",
            "ine5",
            "\n"
        )
        val expected = listOf(
            "line1",
            "line2",
            "line3",
            "line4",
            "line5",
        )

        val actual = runBlocking {
            input.asFlow().normalizeLines().toList()
        }

        Assert.assertEquals(expected, actual)
    }
}
