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

    private var PHYSICAL_COST_PER_SECOND = divBy60(divBy60(5)) // $5/hr
    private var VIRTUAL_COST_PER_SECOND = divBy60(divBy60(1))// $1/hr



    fun estimateCosts(billableVirtualSeconds: Long, billablePhysicalSeconds: Long): String {
        return estimateCosts(BigDecimal(billableVirtualSeconds), BigDecimal(billablePhysicalSeconds))
    }

    private fun estimateCosts(billableVirtualSeconds: BigDecimal, billablePhysicalSeconds: BigDecimal): String {
        val virtualCost = billableVirtualSeconds.multiply(VIRTUAL_COST_PER_SECOND).setScale(2, RoundingMode.HALF_UP)
        val physicalCost = billablePhysicalSeconds.multiply(PHYSICAL_COST_PER_SECOND).setScale(2, RoundingMode.HALF_UP)
        val totalCost = (virtualCost + physicalCost).setScale(2, RoundingMode.HALF_UP)

        val billableVirtualTime = prettyTime(billableVirtualSeconds)
        val billablePhysicalTime = prettyTime(billablePhysicalSeconds)
        val totalTime = prettyTime(billableVirtualSeconds + billablePhysicalSeconds)

        val displayPhysical = billablePhysicalSeconds.signum() == 1
        val displayVirtual = billableVirtualSeconds.signum() == 1 // 1 = positive number > 0
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
        val hours = TimeUnit.SECONDS.toHours(remainder)
        val minutes = TimeUnit.SECONDS.toMinutes(remainder)
        val secons = remainder % 60

        return if (hours > 0) {
            "${hours}h ${minutes}m ${secons}s"
        } else if(minutes > 0) {
            "${minutes}m ${secons}s"
        } else {
            "${secons}s"
        }
    }
}
