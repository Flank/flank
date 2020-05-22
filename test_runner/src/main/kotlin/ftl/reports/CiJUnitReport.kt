package ftl.reports

import ftl.args.IArgs
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.xmlToString
import ftl.util.resolveLocalRunPath
import ftl.util.write
import java.nio.file.Paths

object CiJUnitReport : IReport {
    override val extension = ".xml"
    private fun prepareFileName(args: IArgs) = args.ciJUnitResultFile ?: "${CiJUnitReport::class.simpleName}$extension"
    override fun run(matrices: MatrixMap, result: JUnitTestResult?, printToStdout: Boolean, args: IArgs) {
        val output = result.xmlToString()

        if (printToStdout) {
            print(output)
        } else {
            write(matrices, output, args)
        }

        result?.let {
            uploadToGcStorage(it, args)
        }
    }

    override fun reportPath(matrices: MatrixMap, args: IArgs): String {
        val path = resolveLocalRunPath(matrices, args)
        return Paths.get(path, prepareFileName(args)).toString()
    }

    private fun write(matrices: MatrixMap, output: String, args: IArgs) {
        val reportPath = reportPath(matrices, args)
        reportPath.write(output)
    }

    private fun uploadToGcStorage(ciTestResult: JUnitTestResult, args: IArgs) {
        GcStorage.uploadCiJUnitXml(ciTestResult, args)
    }
}
