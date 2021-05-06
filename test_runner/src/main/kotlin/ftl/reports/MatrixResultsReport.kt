package ftl.reports

import flank.common.log
import flank.common.println
import flank.common.startWithNewLine
import ftl.api.JUnitTest
import ftl.args.IArgs
import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.json.asPrintableTable
import ftl.json.isFailed
import ftl.reports.output.log
import ftl.reports.output.outputReport
import ftl.reports.util.IReport
import ftl.reports.util.ReportManager
import java.io.StringWriter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**

Test Results. Always run.

Example:

59 / 100 (0.00%)
41 matrices failed

 **/
object MatrixResultsReport : IReport {
    override val extension = ".txt"

    private val percentFormat by lazy { DecimalFormat("#0.00", DecimalFormatSymbols(Locale.US)) }

    override fun run(matrices: MatrixMap, result: JUnitTest.Result?, printToStdout: Boolean, args: IArgs) {
        val output = generate(matrices)
        if (printToStdout) log(output)
        write(matrices, output, args)
        ReportManager.uploadReportResult(output, args, fileName())
    }

    private fun generate(matrices: MatrixMap): String {
        val total = matrices.map.size
        // unfinished matrix will not be reported as failed since it's still running
        val success = matrices.map.values.count { it.isFailed().not() }
        val failed = total - success
        val successDouble: Double = success.toDouble() / total.toDouble() * 100.0
        val successPercent = percentFormat.format(successDouble)

        var outputData = "$success / $total ($successPercent%)"
        if (failed > 0) outputData += "$failed matrices failed"

        StringWriter().use { writer ->
            writer.println(reportName().startWithNewLine())
            writer.println("$indent$success / $total ($successPercent%)")
            if (failed > 0) {
                writer.println("$indent$failed matrices failed")
                writer.println()
            }

            if (matrices.map.isNotEmpty()) {
                val savedMatrices = matrices.map.values
                outputReport.log(savedMatrices)
                writer.println(savedMatrices.toList().asPrintableTable())
                savedMatrices.printMatricesLinks(writer)
            }

            return writer.toString()
        }
    }

    private fun Collection<SavedMatrix>.printMatricesLinks(writer: StringWriter) = this
        .filter { it.isFailed() }
        .takeIf { it.isNotEmpty() }
        ?.run {
            writer.println("More details are available at:")
            forEach { writer.println(it.webLinkWithoutExecutionDetails.orEmpty()) }
            writer.println()
        }
}
