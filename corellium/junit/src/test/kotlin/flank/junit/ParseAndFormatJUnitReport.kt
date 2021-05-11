package flank.junit

import org.junit.Assert
import org.junit.Test
import java.io.File

class ParseAndFormatJUnitReport {

    @Test
    fun test() {
        val path = RESOURCES + "JUnitReport.xml"
        val expected = File(path).readLines().toTypedArray()

        val parsed = path.parseJUnitReportFromFile()
        val formatted = parsed.formatXmlString().trim().lines().toTypedArray()

        Assert.assertArrayEquals(expected, formatted)
    }
}
