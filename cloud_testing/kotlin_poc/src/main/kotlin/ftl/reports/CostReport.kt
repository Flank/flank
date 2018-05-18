package ftl.reports

import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import ftl.util.Billing
import ftl.util.Utils.println
import ftl.util.Utils.write
import java.io.StringWriter

/**

Calculates cost based on the matrix map. Always run.

Example:


Billable time:	69h 51m
Billable minutes:	4191
Physical device cost:	$349.25
Virtual  device cost:	$69.85

 */
object CostReport : IReport {

    private fun estimate(matrices: MatrixMap): String {
        var totalBillableMinutes = 0L

        matrices.map.values.forEach {
            totalBillableMinutes += it.billableMinutes
        }

        return Billing.estimateCosts(totalBillableMinutes)
    }

    private fun generate(matrices: MatrixMap): String {
        val cost = estimate(matrices)

        StringWriter().use { writer ->
            writer.println(reportName())
            cost.split("\n").forEach { writer.println(indent + it) }
            return writer.toString()
        }
    }

    private fun write(matrices: MatrixMap, output: String) {
        val reportPath = reportPath(matrices) + ".txt"
        reportPath.write(output)
    }

    override fun run(matrices: MatrixMap, testSuite: TestSuite, printToStdout: Boolean) {
        val output = generate(matrices)
        if (printToStdout) print(output)
        write(matrices, output)
    }
}
