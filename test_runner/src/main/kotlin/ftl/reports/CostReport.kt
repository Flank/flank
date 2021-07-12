package ftl.reports

import flank.common.println
import ftl.analytics.sendConfiguration
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

    private fun estimate(args: IArgs, matrices: MatrixMap): String {
        var totalBillableVirtualMinutes = 0L
        var totalBillablePhysicalMinutes = 0L

        matrices.map.values.forEach {
            totalBillableVirtualMinutes += it.billableMinutes.virtual
            totalBillablePhysicalMinutes += it.billableMinutes.physical
        }

        val virtualCost = calculateVirtualCost(totalBillableVirtualMinutes.toBigDecimal())
        val physicalCost = calculatePhysicalCost(totalBillablePhysicalMinutes.toBigDecimal())

        args.sendConfiguration(
            events = mapOf(
                "virtual_cost" to virtualCost,
                "physical_cost" to physicalCost,
                "total_cost" to calculateTotalCost(
                    virtualCost,
                    physicalCost
                )
            ),
            eventName = "devices_cost"
        )

        return estimateCosts(totalBillableVirtualMinutes, totalBillablePhysicalMinutes)
    }

    private fun generate(args: IArgs, matrices: MatrixMap): String {
        val cost = estimate(args, matrices)
        StringWriter().use { writer ->
            writer.println(reportName())
            cost.split("\n").forEach { writer.println(indent + it) }
            return writer.toString() + "\n"
        }
    }

    override fun run(matrices: MatrixMap, result: JUnitTest.Result?, printToStdout: Boolean, args: IArgs) {
        val output = generate(args, matrices)
        if (printToStdout) print(output)
        write(matrices, output, args)
        ReportManager.uploadReportResult(output, args, fileName())
    }
}
