package ftl.reports.util

import ftl.json.MatrixMap
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.resolveLocalRunPath
import java.nio.file.Paths

interface IReport {
    fun run(matrices: MatrixMap, testSuite: JUnitTestResult?, printToStdout: Boolean = false)

    fun reportName(): String {
        return this::class.java.simpleName
    }

    val extension: String

    fun reportPath(matrices: MatrixMap): String {
        val path = resolveLocalRunPath(matrices)
        return Paths.get(path, reportName() + extension).toString()
    }
}
