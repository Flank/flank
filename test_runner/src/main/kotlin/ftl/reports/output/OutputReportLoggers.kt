package ftl.reports.output

import flank.common.OUTPUT_ARGS
import flank.common.OUTPUT_BILLABLE_MINUTES
import flank.common.OUTPUT_COST
import flank.common.OUTPUT_TEST_RESULTS
import flank.common.OUTPUT_WEBLINKS
import flank.common.OutputReportBillableMinutesNode
import flank.common.OutputReportCostNode
import ftl.api.TestMatrix
import ftl.args.IArgs
import ftl.json.MatrixMap
import java.math.BigDecimal

internal fun OutputReport.log(args: IArgs) {
    add(OUTPUT_ARGS, args)
}

internal fun OutputReport.log(matrixMap: MatrixMap) {
    add(OUTPUT_WEBLINKS, matrixMap.map.values.map { it.webLink })
}

internal fun OutputReport.log(matrices: Collection<TestMatrix.Data>) {
    add(
        OUTPUT_TEST_RESULTS,
        matrices.map {
            it.matrixId to mapOf(
                "app" to it.appFileName,
                "test-file" to it.testFileName,
                "test-axises" to it.axes
            )
        }.toMap()
    )
}

internal fun OutputReport.logCosts(
    physicalCost: BigDecimal,
    virtualCost: BigDecimal,
    totalCost: BigDecimal
) {
    add(OUTPUT_COST, OutputReportCostNode(physicalCost, virtualCost, totalCost))
}

internal fun OutputReport.logBillableMinutes(
    billablePhysicalMinutes: BigDecimal,
    billableVirtualMinutes: BigDecimal,
    billableTotalMinutes: BigDecimal
) {
    add(OUTPUT_BILLABLE_MINUTES, OutputReportBillableMinutesNode(billablePhysicalMinutes, billableVirtualMinutes, billableTotalMinutes))
}
