package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.test.util.defaultTestTimeout
import org.junit.Test
import kotlin.math.min

private val timeoutSeconds = timeoutToSeconds(defaultTestTimeout)

class BillingTest {

    @Test
    fun billableMinutes() {
        assertThat(billableMinutes(min(0L, timeoutSeconds))).isEqualTo(1L)
        assertThat(billableMinutes(min(60L, timeoutSeconds))).isEqualTo(1L)
        assertThat(billableMinutes(min(61L, timeoutSeconds))).isEqualTo(2L)
        assertThat(billableMinutes(min(3_555L, timeoutSeconds))).isEqualTo(15L)
    }

    @Test
    fun `when timeout lower then test execution time, billable minutes should fit to timeout minute`() {
        assertThat(billableMinutes(min(120L, 40))).isEqualTo(1L)
        assertThat(billableMinutes(min(60L, 30))).isEqualTo(1L)
        assertThat(billableMinutes(min(61L, 20))).isEqualTo(1L)
        assertThat(billableMinutes(min(3_555L, 120))).isEqualTo(2L)
    }

    @Test
    fun `when timeout higher then test execution time, billable minutes should fit to duration minute`() {
        assertThat(billableMinutes(min(60L, 120L))).isEqualTo(1L)
        assertThat(billableMinutes(min(360, 1000L))).isEqualTo(6L)
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
        val actualReport = estimateCosts(456L, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts physical`() {
        val expectedReport = """
Physical devices
  $10.25 for 2h 3m
""".trim()
        val actualReport = estimateCosts(0, 123L)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts virtual`() {
        val expectedReport = """
Virtual devices
  $7.60 for 7h 36m
""".trim()
        val actualReport = estimateCosts(456L, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts 1m`() {
        val expectedReport = """
Virtual devices
  $0.02 for 1m
""".trim()
        val actualReport = estimateCosts(1, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }

    @Test
    fun `estimateCosts 0m`() {
        val expectedReport = """
No cost. 0m
""".trim()
        val actualReport = estimateCosts(0, 0)
        assertThat(actualReport).isEqualTo(expectedReport)
    }
}
