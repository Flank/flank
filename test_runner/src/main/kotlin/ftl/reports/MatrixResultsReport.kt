package ftl.reports

import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.Outcome
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

    private val percentFormat by lazy { DecimalFormat("#0.00") }

    private fun generate(matrices: MatrixMap): String {
        var total = 0
        var success = 0
        val failureDetails = mutableListOf<Triple<String, String, String>>()
        matrices.map.values.forEach { matrix ->
            total += 1
            when (matrix.outcome) {
                Outcome.success -> success += 1
                else -> failureDetails.add(Triple(matrix.matrixId, matrix.webLink, matrix.outcomeAdditionalDetails))
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
                failureDetails.forEach {
                    writer.println("${indent}Failed matrix:")
                    writer.println("${indent}${indent}Matrix Id: " + it.first)
                    if (!it.third.isNullOrEmpty()) {
                        writer.println("${indent}${indent}Reason: " + it.third)
                    }
                    writer.println("${indent}${indent}Web Link: " + it.second)
                    writer.println()
                }
            }

            return writer.toString()
        }
    }

    private fun write(matrices: MatrixMap, output: String) {
        val reportPath = reportPath(matrices) + ".txt"
        reportPath.write(output)
    }

    override fun run(matrices: MatrixMap, testSuite: JUnitTestResult?, printToStdout: Boolean) {
        val output = generate(matrices)
        if (printToStdout) print(output)
        write(matrices, output)
    }
}
