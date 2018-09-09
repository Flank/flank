package ftl.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

object Billing {

    private fun divBy60(value: Long): BigDecimal {
        return BigDecimal(value).divide(BigDecimal(60), 10, RoundingMode.HALF_UP)
    }

    private fun divBy60(value: BigDecimal): BigDecimal {
        return value.divide(BigDecimal(60), 10, RoundingMode.HALF_UP)
    }

    private var PHYSICAL_COST_PER_MIN = divBy60(5) // $5/hr
    private var VIRTUAL_COST_PER_MIN = divBy60(1) // $1/hr

    fun billableMinutes(testDurationSeconds: Long): Long {
        return billableMinutes(BigDecimal(testDurationSeconds))
    }

    private fun billableMinutes(testDurationSeconds: BigDecimal): Long {
        val billableMinutes = divBy60(checkForZero(testDurationSeconds))
        // round decimals up.  0.01 minutes is billable at 1 minute.
        return billableMinutes.setScale(0, RoundingMode.UP).longValueExact()
    }

    private fun checkForZero(testDurationSeconds: BigDecimal): BigDecimal {
        // 0s duration => 1s
        if (testDurationSeconds.compareTo(BigDecimal(0)) == 0) {
            return BigDecimal(1)
        }

        return testDurationSeconds
    }

    fun estimateCosts(billableVirtualMinutes: Long, billablePhysicalMinutes: Long): String {
        return estimateCosts(BigDecimal(billableVirtualMinutes), BigDecimal(billablePhysicalMinutes))
    }

    private fun estimateCosts(billableVirtualMinutes: BigDecimal, billablePhysicalMinutes: BigDecimal): String {
        val virtualCost = billableVirtualMinutes.multiply(VIRTUAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP)
        val physicalCost = billablePhysicalMinutes.multiply(PHYSICAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP)
        val totalCost = (virtualCost + physicalCost).setScale(2, RoundingMode.HALF_UP)

        val virtualTime = prettyTime(billableVirtualMinutes)
        val physicalTime = prettyTime(billablePhysicalMinutes)
        val totalTime = prettyTime(billableVirtualMinutes + billablePhysicalMinutes)
        val tab = "\t"

        val displayPhysical = billablePhysicalMinutes.signum() == 1
        val displayVirtual = billableVirtualMinutes.signum() == 1 // 1 = positive number > 0
        val displayTotal = displayPhysical && displayVirtual
        var result = ""

        if (displayPhysical) {
            result += """
Physical devices
  Billable time:$tab$physicalTime
  Billable minutes:$tab$billablePhysicalMinutes
  Cost:$tab${'$'}$physicalCost
"""
        }

        if (displayVirtual) {
            result += """
Virtual devices
  Billable time:$tab$virtualTime
  Billable minutes:$tab$billableVirtualMinutes
  Cost:$tab${'$'}$virtualCost
"""
        }

        if (displayTotal) {
            result += """
Total
  Billable time:$tab$totalTime
  Billable minutes:$tab${billableVirtualMinutes + billablePhysicalMinutes}
  Cost:$tab${'$'}$totalCost
"""
        }

        return result.trim()
    }

    private fun prettyTime(billableMinutes: BigDecimal): String {
        val remainder = billableMinutes.toLong()
        val hours = TimeUnit.MINUTES.toHours(remainder)
        val minutes = remainder % 60
        return "${hours}h ${minutes}m"
    }
}
