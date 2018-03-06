package ftl.reports

import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import ftl.util.Outcome
import java.io.File
import java.text.DecimalFormat

/**

Test Results

Example:

59 / 100 (0.00%)
41 matrices failed

 **/
object MatrixReport : IReport {

    private val percentFormat by lazy { DecimalFormat("#0.00") }

    override fun run(matrices: MatrixMap, testSuite: TestSuite) {
        var total = 0
        var success = 0
        matrices.map.values.forEach { result ->
            total += 1
            if (result.outcome == Outcome.success) success += 1
        }
        val failed = total - success
        val successDouble: Double = success.toDouble() / total.toDouble() * 100.0
        val successPercent = percentFormat.format(successDouble)

        var outputData = "$success / $total ($successPercent%)\n"
        if (failed > 0) outputData += "$failed matrices failed"

        File("${reportPath(matrices)}.csv").printWriter().use { writer ->
            writer.println(reportName())
            outputData.split("\n").forEach { writer.println(indent + it) }
        }
    }
}
