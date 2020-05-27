package ftl.reports

import ftl.args.IArgs
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.xmlToString
import java.io.File

object JUnitReport : IReport {
    override val extension = ".xml"
    override fun run(matrices: MatrixMap, result: JUnitTestResult?, printToStdout: Boolean, args: IArgs) {
        if (result == null) {
            return
        }
        val output = result.xmlToString()

        if (printToStdout) {
            print(output)
        } else {
            write(matrices, output, args)
        }
        GcStorage.uploadCiJUnitXml(result, args, reportPath(matrices, args).let(::File).name)
    }
}
