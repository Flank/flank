package ftl.reports

import ftl.args.IArgs
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import flank.common.log
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.xmlToString

object JUnitReport : IReport {
    override val extension = ".xml"
    override fun run(matrices: MatrixMap, result: JUnitTestResult?, printToStdout: Boolean, args: IArgs) {
        if (result == null) {
            return
        }
        val output = result.xmlToString()

        if (printToStdout) {
            log(output)
        } else {
            write(matrices, output, args)
        }
        GcStorage.uploadReportResult(result.xmlToString(), args, fileName())
    }
}
