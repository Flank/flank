package ftl.reports

import ftl.api.JUnitTest
import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.reports.util.IReport
import ftl.reports.util.ReportManager
import ftl.run.common.prettyPrint
import ftl.util.calculatePhysicalCost
import ftl.util.calculateTotalCost
import ftl.util.calculateVirtualCost

/** Calculates cost based on the matrix map. Always run. */
object JsonCostReport : IReport {

    override val extension = ".json"

    private fun estimate(matrices: MatrixMap): Map<String, Any> {
        var totalBillableVirtualMinutes = 0L
        var totalBillablePhysicalMinutes = 0L

        matrices.map.values.forEach {
            totalBillableVirtualMinutes += it.billableMinutes.virtual
            totalBillablePhysicalMinutes += it.billableMinutes.physical
        }
        val virtualCost = calculateVirtualCost(totalBillableVirtualMinutes.toBigDecimal())
        val physicalCost = calculatePhysicalCost(totalBillablePhysicalMinutes.toBigDecimal())
        val total = calculateTotalCost(virtualCost, physicalCost)

        return mapOf(
            "currency" to "USD",
            "cost" to mapOf(
                "virtual" to virtualCost,
                "physical" to physicalCost,
                "total" to total
            ),
            "billable-time" to mapOf(
                "virtual" to totalBillableVirtualMinutes,
                "physical" to totalBillablePhysicalMinutes,
                "total" to totalBillableVirtualMinutes + totalBillablePhysicalMinutes
            )
        )
    }

    private fun generate(matrices: MatrixMap): String {
        val cost = estimate(matrices)
        return prettyPrint.toJson(cost)
    }

    override fun run(matrices: MatrixMap, result: JUnitTest.Result?, printToStdout: Boolean, args: IArgs) {
        val output = generate(matrices)
        if (printToStdout) print(output)
        write(matrices, output, args)
        ReportManager.uploadReportResult(output, args, fileName())
    }
}
