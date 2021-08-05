package ftl.reports

import flank.common.println
import flank.tool.analytics.mixpanel.Mixpanel
import ftl.api.JUnitTest
import ftl.args.IArgs
import ftl.config.FtlConstants.indent
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.ReportManager
import ftl.util.calculatePhysicalCost
import ftl.util.calculateTotalCost
import ftl.util.calculateVirtualCost
import ftl.util.estimateCosts
import java.io.StringWriter

/** Calculates cost based on the matrix map. Always run. */
object CostReport : IReport {

    override val extension = ".txt"

    private fun estimate(matrices: MatrixMap): String {
        var totalBillableVirtualMinutes = 0L
        var totalBillablePhysicalMinutes = 0L

        matrices.map.values.forEach {
            totalBillableVirtualMinutes += it.billableMinutes.virtual
            totalBillablePhysicalMinutes += it.billableMinutes.physical
        }
        val virtualCost = calculateVirtualCost(totalBillableVirtualMinutes.toBigDecimal())
        val physicalCost = calculatePhysicalCost(totalBillablePhysicalMinutes.toBigDecimal())
        val total = calculateTotalCost(virtualCost, physicalCost)

        Mixpanel.add(
            "cost",
            mapOf(
                "virtual" to virtualCost,
                "physical" to physicalCost,
                "total" to total
            )
        )

        Mixpanel.add(
            "test_duration",
            mapOf(
                "virtual" to totalBillableVirtualMinutes,
                "physical" to totalBillablePhysicalMinutes,
                "total" to totalBillableVirtualMinutes + totalBillablePhysicalMinutes
            )
        )
        return estimateCosts(totalBillableVirtualMinutes, totalBillablePhysicalMinutes)
    }

    private fun generate(matrices: MatrixMap): String {
        val cost = estimate(matrices)
        StringWriter().use { writer ->
            writer.println(reportName())
            cost.split("\n").forEach { writer.println(indent + it) }
            return writer.toString() + "\n"
        }
    }

    override fun run(matrices: MatrixMap, result: JUnitTest.Result?, printToStdout: Boolean, args: IArgs) {
        val output = generate(matrices)
        if (printToStdout) print(output)
        write(matrices, output, args)
        ReportManager.uploadReportResult(output, args, fileName())
    }
}
