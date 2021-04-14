package ftl.reports.utils

import com.google.common.truth.Truth.assertThat
import com.google.testing.model.TestExecution
import flank.common.isWindows
import ftl.adapter.google.GcStorage
import ftl.args.AndroidArgs
import ftl.json.validate
import ftl.reports.CostReport
import ftl.reports.FullJUnitReport
import ftl.reports.JUnitReport
import ftl.reports.MatrixResultsReport
import ftl.reports.api.createJUnitTestResult
import ftl.reports.api.refreshMatricesAndGetExecutions
import ftl.reports.util.ReportManager
import ftl.reports.util.getMatrixPath
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.parseOneSuiteXml
import ftl.run.common.matrixPathToObj
import ftl.run.exception.FTLError
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import java.io.File

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
        mockkStatic("ftl.reports.api.ProcessFromApiKt")
        every { refreshMatricesAndGetExecutions(any(), any()) } returns emptyList()
        ReportManager.generate(matrix, mockArgs, emptyList())
        matrix.validate()
    }

    @Test
    fun `generate fromSuccessResult`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = mockk<AndroidArgs>(relaxed = true)
        every { mockArgs.smartFlankGcsPath } returns ""
        every { mockArgs.useLegacyJUnitResult } returns true
        mockkStatic("ftl.reports.api.ProcessFromApiKt")
        every { refreshMatricesAndGetExecutions(any(), any()) } returns emptyList()
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
        every { mockArgs.project } returns "project"
        return mockArgs
    }

    @Test
    fun `JUnit results should not be uploaded if junit result is null`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/empty_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        every { mockArgs.localResultDir } returns "./src/test/kotlin/ftl/fixtures/empty_result"
        mockkObject(GcStorage)
        ReportManager.generate(matrix, mockArgs, emptyList())
        verify(exactly = 0) { GcStorage.uploadJunitXml(any(), any()) }
    }

    @Test
    fun `JUnit results should not be uploaded if junit result is empty`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/empty_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        every { mockArgs.localResultDir } returns "./src/test/kotlin/ftl/fixtures/empty_result"
        every { mockArgs.useLegacyJUnitResult } returns false
        mockkObject(GcStorage)
        ReportManager.generate(matrix, mockArgs, emptyList())
        verify(exactly = 0) { GcStorage.uploadJunitXml(any(), any()) }
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
    fun `uploadJunitXml should be called when legacy junit disabled`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        every { mockArgs.useLegacyJUnitResult } returns false
        every { mockArgs.project } returns "projecId"

        val executions = emptyList<TestExecution>()
        mockkStatic("ftl.reports.api.ProcessFromApiKt")
        mockkStatic("ftl.reports.api.CreateJUnitTestResultKt")
        every { refreshMatricesAndGetExecutions(any(), any()) } returns executions
        every { executions.createJUnitTestResult(any()) } returns JUnitTestResult(mutableListOf(suite))

        val junitTestResult = ReportManager.processXmlFromFile(matrix, mockArgs, ::parseOneSuiteXml)
        ReportManager.generate(matrix, mockArgs, emptyList())
        verify { GcStorage.uploadJunitXml(junitTestResult!!, mockArgs) }
    }

    @Test
    fun `uploadJunitXml should be called when full junit enabled`() {
        val matrix = matrixPathToObj("./src/test/kotlin/ftl/fixtures/success_result", AndroidArgs.default())
        val mockArgs = prepareMockAndroidArgs()
        every { mockArgs.useLegacyJUnitResult } returns false
        every { mockArgs.fullJUnitResult } returns true
        every { mockArgs.project } returns "projecId"

        val executions = emptyList<TestExecution>()
        mockkStatic("ftl.reports.api.ProcessFromApiKt")
        mockkStatic("ftl.reports.api.CreateJUnitTestResultKt")
        every { refreshMatricesAndGetExecutions(any(), any()) } returns executions
        every { executions.createJUnitTestResult(any()) } returns JUnitTestResult(mutableListOf(suite))

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
    fun `uploadResults shouldn't be called when disable-results-upload set`() {
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
    fun `uploadResults without FullJUnit shouldn't be called`() {
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
        val oldRunSuite =
            JUnitTestSuite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", oldRunTestCases, null, null, null)
        val oldTestResult = JUnitTestResult(mutableListOf(oldRunSuite))

        val newRunTestCases = mutableListOf(
            JUnitTestCase("a", "a", "9.0"),
            JUnitTestCase("b", "b", "21.0"),
            JUnitTestCase("c", "c", "30.0")
        )
        val newRunSuite =
            JUnitTestSuite("", "-1", "-1", -1, "-1", "-1", "-1", "-1", "-1", "-1", newRunTestCases, null, null, null)
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

    @Test
    fun `should get weblink from legacy path and ios path`() {
        assumeFalse(isWindows) // TODO investigate as to why the pathing fails here completely
        val legacyPath =
            File("results/2020-08-06_12-08-55.641213_jGpY/matrix_0/NexusLowRes-28-en-portrait/test_result_1.xml")
        val iosPath = File("results/test_dir/shard_0/iphone8-12.0-en-portrait/test_result_0.xml")
        assertEquals(
            "2020-08-06_12-08-55.641213_jGpY/matrix_0",
            legacyPath.getMatrixPath("2020-08-06_12-08-55.641213_jGpY")
        )
        assertEquals("test_dir/shard_0", iosPath.getMatrixPath("test_dir"))
    }

    @Test
    fun `shouldn't contains multiple test_dir in MatrixPath`() {
        assumeFalse(isWindows) // TODO investigate as to why the pathing fails here completely
        val path = File("results/test_dir/test_dir/shard_0/iphone8-12.0-en-portrait/test_result_0.xml")
        assertEquals("test_dir/shard_0", path.getMatrixPath("test_dir"))
    }

    private val suite: JUnitTestSuite
        get() = JUnitTestSuite(
            name = "any",
            tests = "2",
            failures = "0",
            errors = "0",
            skipped = null,
            time = "0.12",
            timestamp = "123:456",
            testcases = null
        )
}
