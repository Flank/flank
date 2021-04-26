package ftl.adapter.google

import com.google.api.services.toolresults.model.AppStartTime
import com.google.api.services.toolresults.model.CPUInfo
import com.google.api.services.toolresults.model.GraphicsStats
import com.google.api.services.toolresults.model.GraphicsStatsBucket
import com.google.api.services.toolresults.model.MemoryInfo
import com.google.api.services.toolresults.model.PerfEnvironment
import com.google.api.services.toolresults.model.PerfMetricsSummary
import com.google.testing.model.ToolResultsStep
import ftl.api.PerfMetrics
import ftl.util.Duration
import com.google.api.services.toolresults.model.Duration as GoogleApiDuration

internal fun PerfMetrics.Identity.toClientModel(): ToolResultsStep =
    ToolResultsStep()
        .setHistoryId(historyId)
        .setExecutionId(executionId)
        .setStepId(stepId)
        .setProjectId(projectId)

internal fun PerfMetricsSummary.toApiModel(): PerfMetrics.Summary = PerfMetrics.Summary(
    appStartTime = appStartTime?.toApiModel(),
    graphicsStats = graphicsStats?.toApiModel(),
    perfEnvironment = perfEnvironment?.toApiModel(),
    perfMetrics = perfMetrics.orEmpty(),
    executionId = executionId.orEmpty(),
    historyId = historyId.orEmpty(),
    projectId = projectId.orEmpty(),
    stepId = stepId.orEmpty()
)

private fun PerfEnvironment.toApiModel() = PerfMetrics.PerfEnvironment(
    cpuInfo = cpuInfo?.toApiModel(),
    memoryInfo = memoryInfo?.toApiModel()
)

private fun CPUInfo.toApiModel() = PerfMetrics.CPUInfo(cpuProcessor, cpuSpeedInGhz, numberOfCores)

private fun MemoryInfo.toApiModel() = PerfMetrics.MemoryInfo(memoryCapInKibibyte, memoryTotalInKibibyte)

private fun GraphicsStats.toApiModel() = PerfMetrics.GraphicsStats(
    buckets = buckets?.mapNotNull { bucket -> bucket?.toApiModel() },
    highInputLatencyCount = highInputLatencyCount,
    jankyFrames = jankyFrames,
    missedVsyncCount = missedVsyncCount,
    p50Millis = p50Millis,
    p90Millis = p90Millis,
    p95Millis = p95Millis,
    p99Millis = p99Millis,
    slowBitmapUploadCount = slowBitmapUploadCount,
    slowDrawCount = slowDrawCount,
    slowUiThreadCount = slowUiThreadCount,
    totalFrames = totalFrames
)

private fun GraphicsStatsBucket.toApiModel() = PerfMetrics.GraphicsStats.Bucket(frameCount, renderMillis)

private fun AppStartTime.toApiModel() = PerfMetrics.AppStartTime(
    fullyDrawnTime = fullyDrawnTime?.toApiModel(),
    initialDisplayTime = initialDisplayTime?.toApiModel()
)

private fun GoogleApiDuration.toApiModel() = Duration(seconds ?: 0, nanos)
