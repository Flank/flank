package ftl.reports

import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.xmlToString

/** Calculates cost based on the matrix map. Always run. */
object FullJUnitReport : IReport {
    override val extension = ".xml"

    override fun run(matrices: MatrixMap, result: JUnitTestResult?, printToStdout: Boolean, args: IArgs) {
        val output = result.xmlToString()
        if (printToStdout) {
            print(output)
        } else {
            write(matrices, output, args)
        }
    }
}
