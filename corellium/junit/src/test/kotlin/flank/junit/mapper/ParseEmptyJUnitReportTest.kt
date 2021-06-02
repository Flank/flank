package flank.junit.mapper

import flank.junit.JUnit
import flank.junit.RESOURCES
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

/**
 * Various tests for parsing empty test reports in different shapes.
 */
@RunWith(Parameterized::class)
class ParseEmptyJUnitReportTest(
    private val name: String
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            "JUnitReport_empty_1.xml",
            "JUnitReport_empty_2.xml",
            "JUnitReport_empty_3.xml",
        )
    }

    @Test
    fun test() {
        val path = RESOURCES + name
        val parsed = File(path).reader().parseJUnitReport()

        println(parsed)
        Assert.assertEquals(
            JUnit.Report(),
            parsed
        )
    }
}
