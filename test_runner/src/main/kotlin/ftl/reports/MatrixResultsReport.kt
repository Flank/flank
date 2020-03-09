package ftl.reports

import ftl.args.IArgs
import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.println
import ftl.util.write
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
        matrices.map.values.forEach { matrix ->
            total += 1

            // unfinished matrix will not be reported as failed since it's still running
            if (matrix.failed().not()) {
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
            }

            return writer.toString()
        }
    }

    private fun write(matrices: MatrixMap, output: String, args: IArgs) {
        val reportPath = reportPath(matrices, args)
        reportPath.write(output)
    }

    override fun run(matrices: MatrixMap, testSuite: JUnitTestResult?, printToStdout: Boolean, args: IArgs) {
        val output = generate(matrices)
        if (printToStdout) print(output)
        write(matrices, output, args)
    }
}
