package ftl.reports.utils

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.gc.GcStorage
import ftl.reports.CostReport
import ftl.reports.FullJUnitReport
import ftl.reports.JUnitReport
import ftl.reports.MatrixResultsReport
import ftl.reports.util.ReportManager
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.parseOneSuiteXml
import ftl.run.common.matrixPathToObj
import ftl.test.util.FlankTestRunner
import ftl.util.FTLError
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ReportManagerTest {

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()!!

    @After
    fun tearDown() = unmockkAll()

    @Test(expected = FTLError::class)
    fun `generate fromErrorResult`() {
        // TODO: NPE on Windows
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/error_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        every { mockArgs.smartFlankGcsPath } returns ""
        every { mockArgs.useLegacyJUnitResult } returns true
        ReportManager.generate(matrix, mockArgs, emptyList())
    }

    @Test
    fun `generate fromSuccessResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        every { mockArgs.smartFlankGcsPath } returns ""
        every { mockArgs.useLegacyJUnitResult } returns true
        ReportManager.generate(matrix, mockArgs, emptyList())
    }

    private fun prepareMockAndroidArgs(): AndroidArgs {
        val mockArgs = mockk<AndroidArgs>()
        every { mockArgs.smartFlankGcsPath } returns "test"
        every { mockArgs.resultsBucket } returns "test"
        every { mockArgs.resultsDir } returns "test"
        every { mockArgs.useLegacyJUnitResult } returns true
        every { mockArgs.useLocalResultDir() } returns true
        every { mockArgs.localResultDir } returns "./src/test/kotlin/ftl/fixtures/success_result"
        every { mockArgs.flakyTestAttempts } returns 0
        every { mockArgs.disableResultsUpload } returns false
        every { mockArgs.fullJUnitResult } returns false
        every { mockArgs.ignoreFailedTests } returns false
        every { mockArgs.smartFlankDisableUpload } returns true
        return mockArgs
    }

    @Test
    fun `uploadJunitXml should be called`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        val junitTestResult = ReportManager.processXmlFromFile(matrix, mockArgs, ::parseOneSuiteXml)
        ReportManager.generate(matrix, mockArgs, emptyList())
        verify { GcStorage.uploadJunitXml(junitTestResult!!, mockArgs) }
    }

    @Test
    fun `uploadResults should be called`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        mockkObject(GcStorage) {
            every {
                GcStorage.uploadReportResult(any(), any(), any())
            } returns Unit
            ReportManager.generate(matrix, mockArgs, emptyList())
            verify {
                GcStorage.uploadReportResult(any(), mockArgs, CostReport.fileName())
                GcStorage.uploadReportResult(any(), mockArgs, MatrixResultsReport.fileName())
                GcStorage.uploadReportResult(any(), mockArgs, JUnitReport.fileName())
            }
        }
    }

    @Test
    fun `uploadResults should't be called when disable-results-upload set`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        every { mockArgs.disableResultsUpload } returns true
        mockkObject(GcStorage) {
            every {
                GcStorage.uploadReportResult(any(), any(), any())
            } returns Unit
            ReportManager.generate(matrix, mockArgs, emptyList())
            verify(inverse = true) {
                GcStorage.uploadReportResult(any(), mockArgs, CostReport.fileName())
                GcStorage.uploadReportResult(any(), mockArgs, MatrixResultsReport.fileName())
                GcStorage.uploadReportResult(any(), mockArgs, JUnitReport.fileName())
                GcStorage.uploadReportResult(any(), mockArgs, FullJUnitReport.fileName())
            }
        }
    }

    @Test
    fun `uploadResults with FullJUnit should't be called`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        mockkObject(GcStorage) {
            every {
                GcStorage.uploadReportResult(any(), any(), any())
            } returns Unit
            ReportManager.generate(matrix, mockArgs, emptyList())
            verify(inverse = true) {
                GcStorage.uploadReportResult(any(), mockArgs, FullJUnitReport.fileName())
            }
        }
    }

    @Test
    fun `uploadResults with FullJUnit should be called`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        every { mockArgs.fullJUnitResult } returns true
        every { mockArgs.project } returns "test"
        mockkObject(GcStorage) {
            every {
                GcStorage.uploadReportResult(any(), any(), any())
            } returns Unit
            ReportManager.generate(matrix, mockArgs, emptyList())
            verify {
                GcStorage.uploadReportResult(any(), mockArgs, FullJUnitReport.fileName())
            }
        }
    }

    @Test
    fun createShardEfficiencyListTest() {
        val oldRunTestCases = mutableListOf(
            JUnitTestCase("a", "a", "10.0"),
            JUnitTestCase("b", "b", "20.0"),
            JUnitTestCase("c", "c", "30.0")
        )
        val oldRunSuite = JUnitTestSuite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", oldRunTestCases, null, null, null)
        val oldTestResult = JUnitTestResult(mutableListOf(oldRunSuite))

        val newRunTestCases = mutableListOf(
            JUnitTestCase("a", "a", "9.0"),
            JUnitTestCase("b", "b", "21.0"),
            JUnitTestCase("c", "c", "30.0")
        )
        val newRunSuite = JUnitTestSuite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", newRunTestCases, null, null, null)
        val newTestResult = JUnitTestResult(mutableListOf(newRunSuite))

        val mockArgs = mockk<AndroidArgs>()

        val testShardChunks = listOf(listOf("class a#a"), listOf("class b#b"), listOf("class c#c"))
        val result = ReportManager.createShardEfficiencyList(oldTestResult, newTestResult, mockArgs, testShardChunks)

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
