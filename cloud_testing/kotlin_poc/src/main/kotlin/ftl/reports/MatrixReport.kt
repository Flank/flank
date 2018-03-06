package ftl.reports

import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import ftl.util.Outcome
import ftl.util.Utils.println
import ftl.util.Utils.write
import java.io.StringWriter
import java.text.DecimalFormat

/**

Test Results

Example:

59 / 100 (0.00%)
41 matrices failed

 **/
object MatrixReport : IReport {

    private val percentFormat by lazy { DecimalFormat("#0.00") }

    private fun generate(matrices: MatrixMap): String {
        var total = 0
        var success = 0
        matrices.map.values.forEach { result ->
            total += 1
            if (result.outcome == Outcome.success) success += 1
        }
        val failed = total - success
        val successDouble: Double = success.toDouble() / total.toDouble() * 100.0
        val successPercent = percentFormat.format(successDouble)

        var outputData = "$success / $total ($successPercent%)"
        if (failed > 0) outputData += "$failed matrices failed"

        StringWriter().use { writer ->
            writer.println(reportName())
            writer.println("$indent$success / $total ($successPercent%)")
            if (failed > 0) writer.println("$indent$failed matrices failed")

            return writer.toString()
        }
    }

    private fun write(matrices: MatrixMap, output: String) {
        val reportPath = reportPath(matrices) + ".txt"
        reportPath.write(output)
    }

    override fun run(matrices: MatrixMap, testSuite: TestSuite, print: Boolean) {
        val output = generate(matrices)
        if (print) println(output)
        write(matrices, output)
    }
}
