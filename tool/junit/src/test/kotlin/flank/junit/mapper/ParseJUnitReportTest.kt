package flank.junit.mapper

import flank.junit.RESOURCES
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.File

class ParseJUnitReportTest {

    @Test
    fun test() {
        val file = File(RESOURCES + "JUnitReport.xml")
        val expected = file.readLines().filter(String::isNotBlank).toTypedArray()

        val parsed = file.reader().parseJUnitReport()
        val formatted = xmlPrettyWriter.writeValueAsString(parsed).lines()
        val actual = formatted.filter(String::isNotBlank).toTypedArray()

        println(formatted)

        assertArrayEquals(expected, actual)
    }
}
