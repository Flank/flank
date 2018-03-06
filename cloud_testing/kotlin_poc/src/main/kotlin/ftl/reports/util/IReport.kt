package ftl.reports.util

import ftl.json.MatrixMap
import java.nio.file.Paths

interface IReport {
    fun run(matrices: MatrixMap, testSuite: TestSuite)

    fun reportName(): String {
        return this::class.java.simpleName
    }

    fun reportPath(matrices: MatrixMap): String {
        return Paths.get(matrices.runPath, reportName()).toString()
    }
}
