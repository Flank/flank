package ftl.reports

import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import ftl.util.Billing
import java.io.File

/**

Calculates cost based on the matrix map

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

    private fun write(matrices: MatrixMap, cost: String) {
        val costSummaryPath = reportPath(matrices) + ".txt"

        File(costSummaryPath).printWriter().use { writer ->
            writer.print(reportName())
            cost.split("\n").forEach { writer.println(indent + it) }
            writer.println()
        }
    }

    override fun run(matrices: MatrixMap, testSuite: TestSuite) {
        val cost = estimate(matrices)
        write(matrices, cost)
    }
}
