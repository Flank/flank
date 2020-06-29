package ftl.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

private val physicalCostPerMinute = divBy60(5) // $5/hr
private val virtualCostPerMinute = divBy60(1) // $1/hr

private fun divBy60(value: Long) = BigDecimal(value).divide(BigDecimal(60), 10, RoundingMode.HALF_UP)

private fun divBy60(value: BigDecimal) = value.divide(BigDecimal(60), 10, RoundingMode.HALF_UP)

// round decimals up. 0.01 minutes is billable at 1 minute.
fun billableMinutes(testDurationSeconds: Long) = divBy60(checkForZero(BigDecimal(testDurationSeconds)))
    .setScale(0, RoundingMode.UP)
    .longValueExact()

// 0s duration => 1s
private fun checkForZero(testDurationSeconds: BigDecimal) =
    if (testDurationSeconds == BigDecimal.ZERO) BigDecimal.ONE
    else testDurationSeconds

fun estimateCosts(billableVirtualMinutes: Long, billablePhysicalMinutes: Long): String {
    return estimateCosts(BigDecimal(billableVirtualMinutes), BigDecimal(billablePhysicalMinutes))
}

private fun estimateCosts(billableVirtualMinutes: BigDecimal, billablePhysicalMinutes: BigDecimal): String {
    val virtualCost = billableVirtualMinutes.multiply(virtualCostPerMinute).setScale(2, RoundingMode.HALF_UP)
    val physicalCost = billablePhysicalMinutes.multiply(physicalCostPerMinute).setScale(2, RoundingMode.HALF_UP)
    val totalCost = (virtualCost + physicalCost).setScale(2, RoundingMode.HALF_UP)

    val billableVirtualTime = prettyTime(billableVirtualMinutes)
    val billablePhysicalTime = prettyTime(billablePhysicalMinutes)
    val totalTime = prettyTime(billableVirtualMinutes + billablePhysicalMinutes)

    val displayPhysical = billablePhysicalMinutes.signum() == 1
    val displayVirtual = billableVirtualMinutes.signum() == 1 // 1 = positive number > 0
    val displayTotal = displayPhysical && displayVirtual
    var result = ""

    if (!displayPhysical && !displayVirtual) {
        result = "No cost. 0m"
    }

    if (displayPhysical) {
        result += """
Physical devices
  $$physicalCost for $billablePhysicalTime
"""
    }

    if (displayVirtual) {
        result += """
Virtual devices
  $$virtualCost for $billableVirtualTime
"""
    }

    if (displayTotal) {
        result += """
Total
  $$totalCost for $totalTime
"""
    }

    return result.trim()
}

private fun prettyTime(billableMinutes: BigDecimal): String {
    val remainder = billableMinutes.toLong()
    val hours = TimeUnit.MINUTES.toHours(remainder)
    val minutes = remainder % 60

    return if (hours > 0) "${hours}h ${minutes}m"
    else "${minutes}m"
}
