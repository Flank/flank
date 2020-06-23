package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BillingTest {

//    @Test
//    fun billableMinutes() {
//        assertThat(Billing.billableMinutes(0L)).isEqualTo(1L)
//        assertThat(Billing.billableMinutes(60L)).isEqualTo(1L)
//        assertThat(Billing.billableMinutes(61L)).isEqualTo(2L)
//        assertThat(Billing.billableMinutes(3_555L)).isEqualTo(60L)
//    }

    @Test
    fun `estimateCosts physicalAndVirtual`() {
        val expectedReport = """
Physical devices
  $0.17 for 2m 3s

Virtual devices
  $0.13 for 7m 36s

Total
  $0.30 for 9m 39s""".trim()
        val actualReport = Billing.estimateCosts(456L, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts physical`() {
        val expectedReport = """
Physical devices
  $0.17 for 2m 3s
""".trim()
        val actualReport = Billing.estimateCosts(0, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts virtual`() {
        val expectedReport = """
Virtual devices
  $0.13 for 7m 36s
""".trim()
        val actualReport = Billing.estimateCosts(456L, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts 1m`() {
        val expectedReport = """
Virtual devices
  $0.02 for 1m 0s
""".trim()
        val actualReport = Billing.estimateCosts(60, 0)
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

    @Test
    fun `estimateCosts 1h`() {
        val expectedReport = """
Virtual devices
  $1.00 for 1h 0m 0s
""".trim()
        val actualReport = Billing.estimateCosts(3600, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts 1h 20m`() {
        val expectedReport = """
Virtual devices
  $1.35 for 1h 20m 59s
""".trim()
        val actualReport = Billing.estimateCosts(4859, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }
}
