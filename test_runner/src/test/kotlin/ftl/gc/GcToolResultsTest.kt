package ftl.gc

import com.google.api.services.testing.model.ToolResultsHistory
import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class GcToolResultsTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `createToolResultsHistory null succeeds`() {
        val args = mockk<AndroidArgs>()
        every { args.project } returns "123"
        every { args.resultsHistoryName } returns null

        val expected = ToolResultsHistory().setProjectId("123")

        assertThat(GcToolResults.createToolResultsHistory(args)).isEqualTo(expected)
    }

    @Test
    fun `createToolResultsHistory succeeds`() {
        val args = mockk<AndroidArgs>()
        every { args.project } returns "123"
        every { args.resultsHistoryName } returns "custom history"

        val expected = ToolResultsHistory()
            .setProjectId("123")
            .setHistoryId("mockId")

        assertThat(GcToolResults.createToolResultsHistory(args)).isEqualTo(expected)
    }
}
