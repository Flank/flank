package ftl.run.platform.android

import ftl.args.FlankRoboDirective
import ftl.args.ShardChunks

sealed class AndroidTestConfig {
    data class Instrumentation(
        val appApkGcsPath: String,
        val testApkGcsPath: String,
        val testRunnerClass: String?,
        val orchestratorOption: String?,
        // sharding
        val disableSharding: Boolean,
        val testShards: ShardChunks,
        val numUniformShards: Int?,
        val keepTestTargetsEmpty: Boolean,
        val environmentVariables: Map<String, String> = emptyMap(),
        val testTargetsForShard: ShardChunks,
        val clientDetails: Map<String, String> = emptyMap()
    ) : AndroidTestConfig()

    data class Robo(
        val appApkGcsPath: String,
        val flankRoboDirectives: List<FlankRoboDirective>?,
        val roboScriptGcsPath: String?
    ) : AndroidTestConfig()

    data class GameLoop(
        val appApkGcsPath: String,
        val testRunnerClass: String?,
        val scenarioNumbers: List<String>,
        val scenarioLabels: List<String>
    ) : AndroidTestConfig()
}
