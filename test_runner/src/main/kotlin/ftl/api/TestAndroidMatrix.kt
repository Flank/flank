package ftl.api

import ftl.adapter.GoogleTestMatrixAndroid
import ftl.args.FlankRoboDirective
import ftl.config.Device

val executeTestMatrixAndroid: TestMatrixAndroid.Execute get() = GoogleTestMatrixAndroid

object TestMatrixAndroid {

    data class Config(
        // args
        val clientDetails: Map<String, String>?,
        val resultsBucket: String,
        val autoGoogleLogin: Boolean,
        val networkProfile: String?,
        val directoriesToPull: List<String>,
        val obbNames: List<String>,
        val environmentVariables: Map<String, String>,
        val autoGrantPermissions: Boolean,
        val testTimeout: String,
        val performanceMetrics: Boolean,
        val recordVideo: Boolean,
        val flakyTestAttempts: Int,
        val failFast: Boolean,
        val project: String,
        val resultsHistoryName: String?,
        val repeatTests: Int,

        // build
        val otherFiles: Map<String, String>,
        val resultsDir: String,
        val devices: List<Device>,
        val additionalApkGcsPaths: List<String>,
        val obbFiles: Map<String, String>,
    )

    sealed class Type {
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
            val testTargetsForShard: ShardChunks
        ) : Type()

        data class Robo(
            val appApkGcsPath: String,
            val flankRoboDirectives: List<FlankRoboDirective>?,
            val roboScriptGcsPath: String?
        ) : Type()

        data class GameLoop(
            val appApkGcsPath: String,
            val testRunnerClass: String?,
            val scenarioNumbers: List<String>,
            val scenarioLabels: List<String>
        ) : Type()
    }

    interface Execute : (Config, List<Type>) -> List<TestMatrix.Data>
}

typealias ShardChunks = List<List<String>>
