package flank.common

import java.math.BigDecimal

data class OutputReportBillableMinutesNode(
    val physical: BigDecimal,
    val virtual: BigDecimal,
    val total: BigDecimal
)
