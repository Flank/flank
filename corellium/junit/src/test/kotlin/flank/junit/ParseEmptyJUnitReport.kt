package flank.junit

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Various tests for parsing empty test reports in different shapes.
 */
@RunWith(Parameterized::class)
class ParseEmptyJUnitReport(
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
        val parsed = path.parseJUnitReportFromFile()

        println(parsed)
        Assert.assertEquals(
            JUnit.Report(),
            parsed
        )
    }
}
