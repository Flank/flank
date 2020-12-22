package ftl.run.model

sealed class IosTestContext(open val matrixGcsPath: String)

data class XcTestContext(
    val xcTestGcsPath: String,
    val xctestrunFileGcsPath: String,
    val xcodeVersion: String,
    val testSpecialEntitlements: Boolean,
    override val matrixGcsPath: String
) : IosTestContext(matrixGcsPath)

data class GameloopTestContext(
    val appGcsPath: String,
    val scenarios: List<Int>,
    override val matrixGcsPath: String
) : IosTestContext(matrixGcsPath)
