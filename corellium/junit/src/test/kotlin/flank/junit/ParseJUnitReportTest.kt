package flank.junit

import flank.junit.mapper.xmlPrettyWriter
import org.junit.Assert
import org.junit.Test
import java.io.File

class ParseJUnitReportTest {

    @Test
    fun test() {
        val path = RESOURCES + "JUnitReport.xml"
        val expected = File(path).readText().trim()

        val parsed = path.parseJUnitReportFromFile()
        val formatted = xmlPrettyWriter.writeValueAsString(parsed).trim()

        println(formatted)

        Assert.assertEquals(expected, formatted)
    }
}
