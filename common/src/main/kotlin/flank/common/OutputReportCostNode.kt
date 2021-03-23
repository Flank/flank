package flank.common

import java.math.BigDecimal

data class OutputReportCostNode(
    val physical: BigDecimal,
    val virtual: BigDecimal,
    val total: BigDecimal
)
