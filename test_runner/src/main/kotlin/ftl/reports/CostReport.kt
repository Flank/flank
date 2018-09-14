package ftl.reports

import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.TestSuite
import ftl.util.Billing
import ftl.util.Utils.println
import ftl.util.Utils.write
import java.io.StringWriter

/** Calculates cost based on the matrix map. Always run. */
object CostReport : IReport {

    private fun estimate(matrices: MatrixMap): String {
        var totalBillableVirtualMinutes = 0L
        var totalBillablePhysicalMinutes = 0L

        matrices.map.values.forEach {
            totalBillableVirtualMinutes += it.billableVirtualMinutes
            totalBillablePhysicalMinutes += it.billablePhysicalMinutes
        }

        return Billing.estimateCosts(totalBillableVirtualMinutes, totalBillablePhysicalMinutes)
    }

    private fun generate(matrices: MatrixMap): String {
        val cost = estimate(matrices)

        StringWriter().use { writer ->
            writer.println(reportName())
            cost.split("\n").forEach { writer.println(indent + it) }
            return writer.toString() + "\n"
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
