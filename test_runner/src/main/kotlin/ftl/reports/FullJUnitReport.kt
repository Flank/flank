package ftl.reports

import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.xmlToString
import ftl.util.write

/** Calculates cost based on the matrix map. Always run. */
object FullJUnitReport : IReport {
    override val extension = ".xml"

    private fun write(matrices: MatrixMap, output: String, args: IArgs) {
        val reportPath = reportPath(matrices, args)
        reportPath.write(output)
    }

    override fun run(matrices: MatrixMap, result: JUnitTestResult?, printToStdout: Boolean, args: IArgs) {
        val output = result.xmlToString()

        if (printToStdout) {
            print(output)
        } else {
            write(matrices, output, args)
        }
    }
}
