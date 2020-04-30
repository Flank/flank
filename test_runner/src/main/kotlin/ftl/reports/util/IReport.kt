package ftl.reports.util

import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.resolveLocalRunPath
import java.nio.file.Paths

interface IReport {
    fun run(matrices: MatrixMap, result: JUnitTestResult?, printToStdout: Boolean = false, args: IArgs)

    fun reportName(): String {
        return this::class.java.simpleName
    }

    val extension: String

    fun reportPath(matrices: MatrixMap, args: IArgs): String {
        val path = resolveLocalRunPath(matrices, args)
        return Paths.get(path, reportName() + extension).toString()
    }
}
