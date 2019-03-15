package ftl.reports.utils

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.reports.util.ReportManager
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.run.TestRunner
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class ReportManagerTest {

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()!!

    @Test
    fun generate_fromErrorResult() {
        val matrix = TestRunner.matrixPathToObj("./src/test/kotlin/ftl/fixtures/error_result")
        val mockArgs = mock(AndroidArgs::class.java)
        `when`(mockArgs.smartFlankGcsPath).thenReturn("")
        ReportManager.generate(matrix, mockArgs)
    }

    @Test
    fun generate_fromSuccessResult() {
        val matrix = TestRunner.matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result")
        val mockArgs = mock(AndroidArgs::class.java)
        `when`(mockArgs.smartFlankGcsPath).thenReturn("")
        ReportManager.generate(matrix, mockArgs)
    }

    @Test
    fun createShardEfficiencyListTest() {
        val oldRunTestCases = mutableListOf(
            JUnitTestCase("a", "a", "10.0"),
            JUnitTestCase("b", "b", "20.0"),
            JUnitTestCase("c", "c", "30.0")
        )
        val oldRunSuite = JUnitTestSuite("", "-1", "-1", "-1", "-1", "-1", "-1", "-1", oldRunTestCases, null, null, null)
        val oldTestResult = JUnitTestResult(mutableListOf(oldRunSuite))

        val newRunTestCases = mutableListOf(
            JUnitTestCase("a", "a", "9.0"),
            JUnitTestCase("b", "b", "21.0"),
            JUnitTestCase("c", "c", "30.0")
        )
        val newRunSuite = JUnitTestSuite("", "-1", "-1", "-1", "-1", "-1", "-1", "-1", newRunTestCases, null, null, null)
        val newTestResult = JUnitTestResult(mutableListOf(newRunSuite))

        val mockArgs = mock(AndroidArgs::class.java)

        `when`(mockArgs.testShardChunks).thenReturn(listOf(listOf("class a#a"), listOf("class b#b"), listOf("class c#c")))
        val result = ReportManager.createShardEfficiencyList(oldTestResult, newTestResult, mockArgs)

        val expected = listOf(
            ReportManager.ShardEfficiency("Shard 0", 10.0, 9.0, -1.0),
            ReportManager.ShardEfficiency("Shard 1", 20.0, 21.0, 1.0),
            ReportManager.ShardEfficiency("Shard 2", 30.0, 30.0, 0.0)
        )

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `Test getDeviceString`() {
        assertThat(ReportManager.getDeviceString("NexusLowRes-28-en-portrait-rerun_1"))
            .isEqualTo("NexusLowRes-28-en-portrait")

        assertThat(ReportManager.getDeviceString("NexusLowRes-28-en-portrait"))
            .isEqualTo("NexusLowRes-28-en-portrait")

        assertThat(ReportManager.getDeviceString(""))
            .isEqualTo("")
    }
}
