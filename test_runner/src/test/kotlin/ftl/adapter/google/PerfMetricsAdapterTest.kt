package ftl.adapter.google

import com.google.api.services.toolresults.model.AppStartTime
import com.google.api.services.toolresults.model.CPUInfo
import com.google.api.services.toolresults.model.GraphicsStats
import com.google.api.services.toolresults.model.GraphicsStatsBucket
import com.google.api.services.toolresults.model.MemoryInfo
import com.google.api.services.toolresults.model.PerfEnvironment
import com.google.api.services.toolresults.model.PerfMetricsSummary
import com.google.common.truth.Truth.assertThat
import com.google.api.services.testing.model.ToolResultsStep
import ftl.api.PerfMetrics
import ftl.run.common.prettyPrint
import ftl.util.Duration
import org.junit.Test
import com.google.api.services.toolresults.model.Duration as GoogleApiDuration

class PerfMetricsAdapterTest {

    @Test
    fun `should properly map identity to client model`() {
        // given
        val expected = ToolResultsStep()
            .setHistoryId("historyId")
            .setExecutionId("executionId")
            .setStepId("stepId")
            .setProjectId("projectId")
        val testIdentity = PerfMetrics.Identity("executionId", "historyId", "projectId", "stepId")

        // when
        val actual = testIdentity.toClientModel()

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should properly map PerfMetricsSummary to api model`() {
        // given
        val expected = PerfMetrics.Summary(
            appStartTime = PerfMetrics.AppStartTime(Duration(0, 5), Duration(32)),
            graphicsStats = PerfMetrics.GraphicsStats(
                listOf(PerfMetrics.GraphicsStats.Bucket(32, 1000)),
                3,
                1,
                12,
                50,
                90,
                95,
                99,
                24,
                20,
                30,
                60
            ),
            perfEnvironment = PerfMetrics.PerfEnvironment(
                PerfMetrics.CPUInfo("super", 2.4f, 8),
                PerfMetrics.MemoryInfo(1024, 2048)
            ),
            perfMetrics = listOf("test", "unit", "value"),
            executionId = "execution",
            historyId = "history",
            projectId = "project",
            stepId = "step"
        )
        val testSummary = PerfMetricsSummary()
            .setAppStartTime(
                AppStartTime()
                    .setFullyDrawnTime(GoogleApiDuration().setNanos(5))
                    .setInitialDisplayTime(GoogleApiDuration().setSeconds(32))
            )
            .setGraphicsStats(
                GraphicsStats()
                    .setBuckets(listOf(GraphicsStatsBucket().setFrameCount(32).setRenderMillis(1000)))
                    .setHighInputLatencyCount(3)
                    .setJankyFrames(1)
                    .setMissedVsyncCount(12)
                    .setP50Millis(50)
                    .setP90Millis(90)
                    .setP95Millis(95)
                    .setP99Millis(99)
                    .setSlowBitmapUploadCount(24)
                    .setSlowDrawCount(20)
                    .setSlowUiThreadCount(30)
                    .setTotalFrames(60)
            )
            .setPerfEnvironment(
                PerfEnvironment()
                    .setCpuInfo(CPUInfo().setCpuProcessor("super").setCpuSpeedInGhz(2.4f).setNumberOfCores(8))
                    .setMemoryInfo(MemoryInfo().setMemoryCapInKibibyte(1024).setMemoryTotalInKibibyte(2048))
            )
            .setPerfMetrics(listOf("test", "unit", "value"))
            .setExecutionId("execution")
            .setHistoryId("history")
            .setProjectId("project")
            .setStepId("step")

        // when
        val actual = testSummary.toApiModel()

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `PerfMetrics Summary json should match gcloud json`() {
        // given
        val gcloudJson = """
            {
              "appStartTime": {
                "fullyDrawnTime": {
                  "seconds": 5
                },
                "initialDisplayTime": {
                  "seconds": 4
                }
              },
              "graphicsStats": {
                "p90Millis": 100
              },
              "perfEnvironment": {
                "cpuInfo": {
                  "cpuProcessor": "mock_processor",
                  "numberOfCores": 16
                },
                "memoryInfo": {
                  "memoryCapInKibibyte": 1024
                }
              },
              "perfMetrics": [],
              "executionId": "1",
              "historyId": "2",
              "projectId": "3",
              "stepId": "4"
            }
        """.trimIndent()
        val summary = PerfMetrics.Summary(
            appStartTime = PerfMetrics.AppStartTime(Duration(5), Duration(4)),
            graphicsStats = PerfMetrics.GraphicsStats(p90Millis = 100),
            perfEnvironment = PerfMetrics.PerfEnvironment(
                PerfMetrics.CPUInfo("mock_processor", numberOfCores = 16),
                PerfMetrics.MemoryInfo(1024)
            ),
            perfMetrics = emptyList(),
            executionId = "1",
            historyId = "2",
            projectId = "3",
            stepId = "4"
        )

        // when
        val actual = prettyPrint.toJson(summary)

        // then
        assertThat(actual).isEqualTo(gcloudJson)
    }
}
