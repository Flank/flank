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

    fun estimateCosts(billableMinutes: Long): String {
        return estimateCosts(BigDecimal(billableMinutes))
    }

    private fun estimateCosts(billableMinutes: BigDecimal): String {
        val physicalCost = billableMinutes.multiply(PHYSICAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP)
        val virtualCost = billableMinutes.multiply(VIRTUAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP)

        val remainder = billableMinutes.toLong()
        val hours = TimeUnit.MINUTES.toHours(remainder)
        val minutes = remainder % 60
        val tab = "\t"

        return """
Billable time:$tab${hours}h ${minutes}m
Billable minutes:$tab$billableMinutes
Physical device cost:$tab$$physicalCost
Virtual  device cost:$tab$$virtualCost"""
    }
}
