package ftl.config

data class RunConfig(
        // Note: Orchestrator breaks screenshot support.
        // FTL will not pull /sdcard/screenshots if orchestrator is enabled
        // even when that dir is specified explicitly: TestSetup().setDirectoriesToPull(listOf("/sdcard/screenshots"))
        val useOrchestrator: String = "USE_ORCHESTRATOR", // must be USE_ORCHESTRATOR or null
        val disablePerformanceMetrics: Boolean = true,
        // Disabling video recording causes infrastructure failures because the AVD idles and disconnects.
        // Until this is resolved, video recording must be enabled.
        val disableVideoRecording: Boolean = false,
        val testTimeoutMinutes: Long = 5
)
