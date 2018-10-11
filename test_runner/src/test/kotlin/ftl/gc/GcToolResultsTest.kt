package ftl.gc

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
    fun getHistoryId_null_succeeds() {
        val args = mock(AndroidArgs::class.java)
        `when`(args.projectId).thenReturn("123")

        assertThat(GcToolResults.createHistoryId(args)).isNull()
    }

    @Test
    fun getHistoryId_succeeds() {
        val args = mock(AndroidArgs::class.java)
        `when`(args.projectId).thenReturn("123")
        `when`(args.resultsHistoryName).thenReturn("custom history")

        assertThat(GcToolResults.createHistoryId(args)).isEqualTo("mockId")
    }
}
