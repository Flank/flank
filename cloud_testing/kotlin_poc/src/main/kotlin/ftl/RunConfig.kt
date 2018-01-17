package ftl

data class RunConfig(
        val useOrchestrator: String = "USE_ORCHESTRATOR", // must be USE_ORCHESTRATOR or null
        val disablePerformanceMetrics: Boolean = true,
        val disableVideoRecording: Boolean = true,
        val testTimeoutMinutes: Long = 5
)
