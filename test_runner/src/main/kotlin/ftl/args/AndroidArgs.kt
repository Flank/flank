package ftl.args

import ftl.args.yml.AppTestPair
import ftl.config.Device

data class AndroidArgs(
    val commonArgs: CommonArgs,
    val appApk: String?,
    val testApk: String?,
    val additionalApks: List<String>,
    val autoGoogleLogin: Boolean,
    val useOrchestrator: Boolean,
    val roboDirectives: List<FlankRoboDirective>,
    val roboScript: String?,
    val environmentVariables: Map<String, String>,
    val directoriesToPull: List<String>,
    val otherFiles: Map<String, String>,
    val performanceMetrics: Boolean,
    val numUniformShards: Int?,
    val testRunnerClass: String?,
    val testTargets: List<String>,
    val devices: List<Device>,
    val additionalAppTestApks: List<AppTestPair>
) : IArgs by commonArgs {
    companion object : AndroidArgsCompanion()
}

val AndroidArgs.isInstrumentationTest
    get() = appApk != null && testApk != null ||
            additionalAppTestApks.isNotEmpty() &&
            (appApk != null || additionalAppTestApks.all { (app, _) -> app != null })

val AndroidArgs.isRoboTest
    get() = appApk != null &&
            (roboDirectives.isNotEmpty() || roboScript != null)
