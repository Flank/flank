package flank.junit.mapper

import flank.junit.JUnit
import flank.junit.RESOURCES
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.lang.IllegalArgumentException

class FailOnInvalidJUnitReportTest {

    @Test(expected = IllegalArgumentException::class)
    fun test() {
        val file = File(RESOURCES + "JUnitReport_invalid.xml")
        val parsed = file.reader().parseJUnitReport()

        println(parsed)
        Assert.assertEquals(
            JUnit.Report(),
            parsed
        )
    }
}
