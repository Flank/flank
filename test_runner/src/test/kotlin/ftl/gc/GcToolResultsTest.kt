package ftl.gc

import com.google.api.services.testing.model.ToolResultsHistory
import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GcToolResultsTest {

    @Test
    fun createToolResultsHistory_null_succeeds() {
        val args = mock(AndroidArgs::class.java)
        `when`(args.project).thenReturn("123")

        val expected = ToolResultsHistory().setProjectId("123")

        assertThat(GcToolResults.createToolResultsHistory(args)).isEqualTo(expected)
    }

    @Test
    fun createToolResultsHistory_succeeds() {
        val args = mock(AndroidArgs::class.java)
        `when`(args.project).thenReturn("123")
        `when`(args.resultsHistoryName).thenReturn("custom history")

        val expected = ToolResultsHistory()
            .setProjectId("123")
            .setHistoryId("mockId")

        assertThat(GcToolResults.createToolResultsHistory(args)).isEqualTo(expected)
    }
}
