package ftl.reports

import flank.common.log
import ftl.api.JUnitTest
import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.ReportManager

object JUnitReport : IReport {
    override val extension = ".xml"
    override fun run(matrices: MatrixMap, result: JUnitTest.Result?, printToStdout: Boolean, args: IArgs) {
        if (result == null) {
            return
        }
        val output = result.toXmlString()

        if (printToStdout) {
            log(output)
        } else {
            write(matrices, output, args)
        }
        ReportManager.uploadReportResult(result.toXmlString(), args, fileName())
    }
}
