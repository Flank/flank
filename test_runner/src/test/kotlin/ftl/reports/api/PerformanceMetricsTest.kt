package ftl.reports.api

import com.google.api.services.testing.model.AndroidDevice
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.ToolResultsStep
import com.google.common.truth.Truth.assertThat
import ftl.android.AndroidCatalog
import ftl.args.IArgs
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class PerformanceMetricsTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should not get and upload performance metrics for virtual devices`() {
        mockkObject(AndroidCatalog) {
            every { AndroidCatalog.isVirtualDevice(any<AndroidDevice>(), any()) } returns true
            val args = mockk<IArgs> {
                every { resultsBucket } returns "b8ce"
            }

           assertThat(testExecutions.map { it to "path" }.getAndUploadPerformanceMetrics(args)).isEmpty()
        }
    }

    @Test
    fun `should get and upload performance metrics for physical devices if results upload enabled`() {
        val expectedBucket = "bucket"
        val expectedPath = "path"

        mockkObject(AndroidCatalog) {
            every { AndroidCatalog.isVirtualDevice(any<AndroidDevice>(), any()) } returns false
            val performanceMetrics = testExecutions.map {
                GcToolResults.getPerformanceMetric(it.toolResultsStep)
            }

            mockkObject(GcStorage) {
                val args = mockk<IArgs> {
                    every { resultsBucket } returns expectedBucket
                    every { useLocalResultDir() } returns false
                    every { localResultDir } returns "local"
                    every { disableResultsUpload } returns false
                }
                testExecutions.map { it to expectedPath }.getAndUploadPerformanceMetrics(args)
                performanceMetrics.forEach {
                    verify { GcStorage.uploadPerformanceMetrics(it, expectedBucket, expectedPath) }
                }
            }
        }
    }

    @Test
    fun `should get and not upload performance metrics for physical devices if results upload disabled`() {
        val expectedBucket = "bucket"
        val expectedPath = "path"

        mockkObject(AndroidCatalog) {
            every { AndroidCatalog.isVirtualDevice(any<AndroidDevice>(), any()) } returns false
            val performanceMetrics = testExecutions.map {
                GcToolResults.getPerformanceMetric(it.toolResultsStep)
            }

            mockkObject(GcStorage) {
                val args = mockk<IArgs> {
                    every { resultsBucket } returns expectedBucket
                    every { useLocalResultDir() } returns false
                    every { localResultDir } returns "local"
                    every { disableResultsUpload } returns true
                }
                testExecutions.map { it to expectedPath }.getAndUploadPerformanceMetrics(args)
                performanceMetrics.forEach {
                    verify(exactly = 0) { GcStorage.uploadPerformanceMetrics(it, expectedBucket, expectedPath) }
                }
            }
        }
    }

    private val testExecutions = (1..5).map {
        mockk<TestExecution> {
            every { projectId } returns "test"
            every { toolResultsStep } returns toolResultsStep(it)
            every { environment } returns mockk {
                every { androidDevice } returns AndroidDevice().setAndroidModelId(it.toString())
            }
        }
    }

    private fun toolResultsStep(id: Int) = ToolResultsStep()
        .setStepId(id.toString())
        .setExecutionId(id.toString())
        .setHistoryId(id.toString())
        .setProjectId(id.toString())
}
