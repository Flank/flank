package ftl.reports.util

import ftl.config.FtlConstants.localResultsDir
import ftl.json.MatrixMap
import java.io.File
import java.nio.file.Paths

interface IReport {
    fun run(matrices: MatrixMap, testSuite: TestSuite)

    fun reportName(): String {
        return this::class.java.simpleName
    }

    fun reportPath(matrices: MatrixMap): String {
        var parent = File(matrices.runPath)
        if (!parent.exists()) parent = Paths.get(localResultsDir, parent.name).toFile()

        return Paths.get(parent.toString(), reportName()).toString()
    }
}
