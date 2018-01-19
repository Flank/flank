package ftl

data class RunConfig(
        val useOrchestrator: String = "USE_ORCHESTRATOR", // must be USE_ORCHESTRATOR or null
        val disablePerformanceMetrics: Boolean = true,
        // Disabling video recording causes infrastructure failures because the AVD idles and disconnects.
        // Until this is resolved, video recording must be enabled.
        val disableVideoRecording: Boolean = false,
        val testTimeoutMinutes: Long = 5
)
