package ftl.reports

import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.Utils.println
import ftl.util.Utils.write
import java.io.StringWriter
import java.text.DecimalFormat

/**

Test Results. Always run.

Example:

59 / 100 (0.00%)
41 matrices failed

 **/
object MatrixResultsReport : IReport {
    override val extension = ".txt"

    private val percentFormat by lazy { DecimalFormat("#0.00") }

    private fun generate(matrices: MatrixMap): String {
        var total = 0
        var success = 0
        val failedMatrices = mutableListOf<SavedMatrix>()
        matrices.map.values.forEach { matrix ->
            total += 1

            if (matrix.failed()) {
                failedMatrices.add(matrix)
            } else {
                success += 1
            }
        }
        val failed = total - success
        val successDouble: Double = success.toDouble() / total.toDouble() * 100.0
        val successPercent = percentFormat.format(successDouble)

        var outputData = "$success / $total ($successPercent%)"
        if (failed > 0) outputData += "$failed matrices failed"

        StringWriter().use { writer ->
            writer.println(reportName())
            writer.println("$indent$success / $total ($successPercent%)")
            if (failed > 0) {
                writer.println("$indent$failed matrices failed")
                writer.println()
                failedMatrices.forEach {
                    writer.println("$indent${it.matrixId} ${it.outcomeDetails}")
                    writer.println("$indent${it.webLink}")
                    writer.println()
                }
            }

            return writer.toString()
        }
    }

    private fun write(matrices: MatrixMap, output: String) {
        val reportPath = reportPath(matrices)
        reportPath.write(output)
    }

    override fun run(matrices: MatrixMap, testSuite: JUnitTestResult?, printToStdout: Boolean) {
        val output = generate(matrices)
        if (printToStdout) print(output)
        write(matrices, output)
    }
}
