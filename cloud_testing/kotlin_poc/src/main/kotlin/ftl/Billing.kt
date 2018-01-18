package ftl

import java.math.BigDecimal
import java.math.RoundingMode

object Billing {

    internal fun divBy60(value: Long): BigDecimal {
        return BigDecimal(value).divide(BigDecimal(60), 10, RoundingMode.HALF_UP)
    }

    internal fun divBy60(value: BigDecimal): BigDecimal {
        return value.divide(BigDecimal(60), 10, RoundingMode.HALF_UP)
    }

    internal var PHYSICAL_COST_PER_MIN = divBy60(5) // $5/hr
    internal var VIRTUAL_COST_PER_MIN = divBy60(1) // $1/hr

    fun billableMinutes(testDurationSeconds: Long): Long {
        return billableMinutes(BigDecimal(testDurationSeconds))
    }

    fun billableMinutes(testDurationSeconds: BigDecimal): Long {
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

    fun estimateCosts(billableMinutes: Long) {
        estimateCosts(BigDecimal(billableMinutes))
    }

    fun estimateCosts(billableMinutes: BigDecimal) {
        val physicalCost = billableMinutes.multiply(PHYSICAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP)
        val virtualCost = billableMinutes.multiply(VIRTUAL_COST_PER_MIN).setScale(2, RoundingMode.HALF_UP)

        println("Billable minutes: " + billableMinutes)
        println("Physical device cost: $$physicalCost")
        println("Virtual  device cost: $$virtualCost")
    }
}
