package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BillingTest {

    @Test
    fun billableMinutes() {
        assertThat(Billing.billableMinutes(0L)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(60L)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(61L)).isEqualTo(2L)
        assertThat(Billing.billableMinutes(3_555L)).isEqualTo(60L)
    }

    @Test
    fun estimateCosts_physicalAndVirtual() {
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
        val actualReport = Billing.estimateCosts(456L, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun estimateCosts_physical() {
        val expectedReport = """
Physical devices
  Billable time:	2h 3m
  Billable minutes:	123
  Cost:	$10.25
""".trim()
        val actualReport = Billing.estimateCosts(0, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun estimateCosts_virtual() {
        val expectedReport = """
Virtual devices
  Billable time:	7h 36m
  Billable minutes:	456
  Cost:	$7.60
""".trim()
        val actualReport = Billing.estimateCosts(456L, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }
}
