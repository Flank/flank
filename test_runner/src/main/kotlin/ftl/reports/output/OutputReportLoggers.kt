package ftl.reports.output

import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import java.math.BigDecimal

internal fun OutputReport.log(args: IArgs) {
    add("args", args)
}

internal fun OutputReport.log(matrixMap: MatrixMap) {
    add("weblinks", matrixMap.map.values.map { it.webLink })
}

internal fun OutputReport.log(matrices: Collection<SavedMatrix>) {
    add(
        "test_results",
        matrices.map {
            it.matrixId to it.testAxises
        }.toMap()
    )
}

internal fun OutputReport.log(
    physicalCost: BigDecimal,
    virtualCost: BigDecimal,
    totalCost: BigDecimal
) {
    add("cost", OutputReportCostNode(physicalCost, virtualCost, totalCost))
}

private data class OutputReportCostNode(
    val physical: BigDecimal,
    val virtual: BigDecimal,
    val total: BigDecimal
)
