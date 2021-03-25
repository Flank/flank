package ftl.reports.output

import flank.common.OUTPUT_ARGS
import flank.common.OUTPUT_COST
import flank.common.OUTPUT_TEST_RESULTS
import flank.common.OUTPUT_WEBLINKS
import flank.common.OutputReportCostNode
import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import java.math.BigDecimal

internal fun OutputReport.log(args: IArgs) {
    add(OUTPUT_ARGS, args)
}

internal fun OutputReport.log(matrixMap: MatrixMap) {
    add(OUTPUT_WEBLINKS, matrixMap.map.values.map { it.webLink })
}

internal fun OutputReport.log(matrices: Collection<SavedMatrix>) {
    add(
        OUTPUT_TEST_RESULTS,
        matrices.map {
            it.matrixId to mapOf(
                "app" to it.appFileName,
                "test-axises" to it.testAxises
            )
        }.toMap()
    )
}

internal fun OutputReport.log(
    physicalCost: BigDecimal,
    virtualCost: BigDecimal,
    totalCost: BigDecimal
) {
    add(OUTPUT_COST, OutputReportCostNode(physicalCost, virtualCost, totalCost))
}
