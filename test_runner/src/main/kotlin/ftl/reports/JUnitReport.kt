package ftl.reports

import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.xmlToString
import ftl.util.Utils.write

/** Calculates cost based on the matrix map. Always run. */
object JUnitReport : IReport {
    private fun write(matrices: MatrixMap, output: String) {
        val reportPath = reportPath(matrices) + ".xml"
        reportPath.write(output)
    }

    override fun run(matrices: MatrixMap, testSuite: JUnitTestResult?, printToStdout: Boolean) {
        val output = testSuite.xmlToString()

        if (printToStdout) {
            print(output)
        } else {
            write(matrices, output)
        }
    }
}
