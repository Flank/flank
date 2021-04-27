package ftl.api

import ftl.adapter.GooglePerformanceMetricsFetch
import ftl.util.Duration

val fetchPerformanceMetrics: PerfMetrics.Fetch get() = GooglePerformanceMetricsFetch

object PerfMetrics {

    data class Summary(
        val appStartTime: AppStartTime? = null,
        val graphicsStats: GraphicsStats? = null,
        val perfEnvironment: PerfEnvironment? = null,
        val perfMetrics: List<String>? = null,
        val executionId: String? = null,
        val historyId: String? = null,
        val projectId: String? = null,
        val stepId: String? = null,
    )

    data class GraphicsStats(
        val buckets: List<Bucket>? = null,
        val highInputLatencyCount: Long? = null,
        val jankyFrames: Long? = null,
        val missedVsyncCount: Long? = null,
        val p50Millis: Long? = null,
        val p90Millis: Long? = null,
        val p95Millis: Long? = null,
        val p99Millis: Long? = null,
        val slowBitmapUploadCount: Long? = null,
        val slowDrawCount: Long? = null,
        val slowUiThreadCount: Long? = null,
        val totalFrames: Long? = null,
    ) {
        data class Bucket(
            val frameCount: Long?,
            val renderMillis: Long?,
        )
    }

    data class AppStartTime(
        val fullyDrawnTime: Duration? = null,
        val initialDisplayTime: Duration? = null,
    )

    data class PerfEnvironment(
        val cpuInfo: CPUInfo? = null,
        val memoryInfo: MemoryInfo? = null,
    )

    data class CPUInfo(
        val cpuProcessor: String? = null,
        val cpuSpeedInGhz: Float? = null,
        val numberOfCores: Int? = null,
    )

    data class MemoryInfo(
        val memoryCapInKibibyte: Long? = null,
        val memoryTotalInKibibyte: Long? = null,
    )

    data class Identity(
        val executionId: String,
        val historyId: String,
        val projectId: String,
        val stepId: String,
    )

    interface Fetch : (Identity) -> Summary
}
