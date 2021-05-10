package flank.junit

import org.junit.Assert
import org.junit.Test
import java.lang.IllegalArgumentException

class FailOnInvalidJUnitReport {

    @Test(expected = IllegalArgumentException::class)
    fun test() {
        val path = RESOURCES + "JUnitReport_invalid.xml"
        val parsed = path.parseJUnitReportFromFile()

        println(parsed)
        Assert.assertEquals(
            JUnit.Report(),
            parsed
        )
    }
}
