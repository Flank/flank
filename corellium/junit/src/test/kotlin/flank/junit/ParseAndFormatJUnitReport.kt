package flank.junit

import org.junit.Assert
import org.junit.Test
import java.io.File

class ParseAndFormatJUnitReport {

    @Test
    fun test() {
        val path = RESOURCES + "JUnitReport.xml"
        val expected = File(path).readText()

        val parsed = path.parseJUnitReportFromFile()
        val formatted = parsed.formatXmlString()

        println(formatted)
        Assert.assertEquals(expected, formatted)
    }
}
