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
  $10.25 for 2h 3m

Virtual devices
  $7.60 for 7h 36m

Total
  $17.85 for 9h 39m""".trim()
        val actualReport = Billing.estimateCosts(456L, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun estimateCosts_physical() {
        val expectedReport = """
Physical devices
  $10.25 for 2h 3m
""".trim()
        val actualReport = Billing.estimateCosts(0, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun estimateCosts_virtual() {
        val expectedReport = """
Virtual devices
  $7.60 for 7h 36m
""".trim()
        val actualReport = Billing.estimateCosts(456L, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun estimateCosts_1m() {
        val expectedReport = """
Virtual devices
  $0.02 for 1m
""".trim()
        val actualReport = Billing.estimateCosts(1, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun estimateCosts_0m() {
        val expectedReport = """
No cost. 0m
""".trim()
        val actualReport = Billing.estimateCosts(0, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }
}
