package flank.junit

import flank.junit.mapper.xmlPrettyWriter
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.File

class ParseJUnitReportTest {

    @Test
    fun test() {
        val path = RESOURCES + "JUnitReport.xml"
        val expected = File(path).readLines().filter(String::isNotBlank).toTypedArray()

        val parsed = path.parseJUnitReportFromFile()
        val formatted = xmlPrettyWriter.writeValueAsString(parsed).lines()
        val actual = formatted.filter(String::isNotBlank).toTypedArray()

        println(formatted)

        assertArrayEquals(expected, actual)
    }
}
