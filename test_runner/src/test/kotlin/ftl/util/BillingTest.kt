package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BillingTest {

    @Test
    fun billableMinutes() {
        assertThat(Billing.billableMinutes(0L, 0)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(60L, 0)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(61L, 0)).isEqualTo(2L)
        assertThat(Billing.billableMinutes(3_555L, 0)).isEqualTo(60L)
    }

    @Test
    fun `when timeout lower then duration billable minutes should fit to timeout minute`() {
        assertThat(Billing.billableMinutes(120L, 40)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(60L, 30)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(61L, 20)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(3_555L, 120)).isEqualTo(2L)
    }

    @Test
    fun `when timeout higher then duration billable minutes should fit to duration minute`() {
        assertThat(Billing.billableMinutes(60L, 120L)).isEqualTo(1L)
        assertThat(Billing.billableMinutes(360, 1000L)).isEqualTo(6L)
    }

    @Test
    fun `estimateCosts physicalAndVirtual`() {
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
    fun `estimateCosts physical`() {
        val expectedReport = """
Physical devices
  $10.25 for 2h 3m
""".trim()
        val actualReport = Billing.estimateCosts(0, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts virtual`() {
        val expectedReport = """
Virtual devices
  $7.60 for 7h 36m
""".trim()
        val actualReport = Billing.estimateCosts(456L, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts 1m`() {
        val expectedReport = """
Virtual devices
  $0.02 for 1m
""".trim()
        val actualReport = Billing.estimateCosts(1, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts 0m`() {
        val expectedReport = """
No cost. 0m
""".trim()
        val actualReport = Billing.estimateCosts(0, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }
}
