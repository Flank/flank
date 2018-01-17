data class RunConfig(
        val useOrchestrator: String = "true",
        val disablePerformanceMetrics: Boolean = true,
        val disableVideoRecording: Boolean = true,
        val testTimeout: String = "5m"
)
