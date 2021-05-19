package ftl.api

import ftl.adapter.GoogleTestMatrixIos
import ftl.config.Device

val executeTestMatrixIos: TestMatrixIos.Execute get() = GoogleTestMatrixIos

object TestMatrixIos {

    data class Config(
        // args
        val clientDetails: Map<String, String>?,
        val networkProfile: String?,
        val directoriesToPull: List<String>,
        val testTimeout: String,
        val recordVideo: Boolean,
        val flakyTestAttempts: Int,
        val failFast: Boolean,
        val project: String,
        val resultsHistoryName: String?,

        // build
        val devices: List<Device>,
        val otherFiles: Map<String, String>,
        val additionalIpasGcsPaths: List<String>,
    )

    sealed class Type(open val matrixGcsPath: String) {
        data class XcTest(
            val xcTestGcsPath: String,
            val xcTestRunFileGcsPath: String,
            val xcodeVersion: String,
            val testSpecialEntitlements: Boolean,
            override val matrixGcsPath: String,
        ) : Type(matrixGcsPath)

        data class GameLoop(
            val appGcsPath: String,
            val scenarios: List<Int>,
            override val matrixGcsPath: String,
        ) : Type(matrixGcsPath)
    }

    interface Execute : (Config, List<Type>) -> List<TestMatrix.Data>
}