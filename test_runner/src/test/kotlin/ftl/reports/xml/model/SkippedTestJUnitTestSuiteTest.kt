package ftl.reports.xml.model

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

internal class SkippedTestJUnitTestSuiteTest {

    @Test
    fun `should created JUnitTestSuite with constant fields for skipped junit test case`() {
        // given
        val testsList = listOf<JUnitTestCase>(mockk())

        // when
        val actual = getSkippedJUnitTestSuite(testsList)

        // then
        assertThat(actual.errors).isEqualTo("0")
        assertThat(actual.failures).isEqualTo("0")
        assertThat(actual.time).isEqualTo("0.0")
        assertThat(actual.name).isEqualTo("junit-ignored")
    }
}
