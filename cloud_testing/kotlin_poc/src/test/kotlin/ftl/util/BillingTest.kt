package ftl.util

import org.junit.Assert.assertEquals
import org.junit.Test

class BillingTest {

    @Test
    fun billableMinutes() {
        assertEquals(1L, Billing.billableMinutes(0L))
        assertEquals(1L, Billing.billableMinutes(60L))
        assertEquals(2L, Billing.billableMinutes(61L))
        assertEquals(60L, Billing.billableMinutes(3_555L))
    }

    @Test
    fun estimateCosts() {
        val expectedReport = """
Physical devices
  Billable time:	2h 3m
  Billable minutes:	123
  Cost:	$10.25
Virtual devices
  Billable time:	7h 36m
  Billable minutes:	456
  Cost:	$7.60
Total
  Billable time:	9h 39m
  Billable minutes:	579
  Cost:	$17.85""".trim()
        assertEquals(expectedReport, Billing.estimateCosts(456L, 123L))
    }
}