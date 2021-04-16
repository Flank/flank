package ftl.environment

import com.google.common.truth.Truth.assertThat
import ftl.util.SystemOutColor
import org.junit.Test

class TestEnvironmentInfoTest {

    @Test
    fun `should properly map tag values to colors`() {
        // given
        val testData = listOf(
            "deprecated=11" to SystemOutColor.RED,
            "default" to SystemOutColor.GREEN,
            "beta" to SystemOutColor.YELLOW,
            "" to SystemOutColor.DEFAULT
        )
        val expectedColors = testData.map { (_, color) -> color }

        // when
        val actualColors = testData.map { (data, _) -> data }.map(tagToSystemOutColorMapper)

        // then
        actualColors.forEachIndexed { index, systemOutColor ->
            assertThat(systemOutColor).isEqualTo(expectedColors[index])
        }
    }
}
