import utils.defaultAndroidOutputPattern
import utils.defaultIosOutputPattern
import utils.getPropertyAsList

data class TestParameters(
    val flankPath: String,
    val ymlPath: String,
    val workingDirectory: String,
    val outputPattern: String,
    val expectedOutputCode: Int,
    val runParams: List<String>
)


fun Map<String, String>.toAndroidTestParameters() = withDefault(
    defaultAndroidOutputPattern,
    listOf("firebase", "test", "android", "run")
)

fun Map<String, String>.toIosParameters() = withDefault(
    defaultIosOutputPattern,
    listOf("firebase", "test", "android", "ios")
)

private fun Map<String, String>.withDefault(outputPattern: String, runParams: List<String>) = TestParameters(
    getOrDefault("flank-path", ""),
    getOrDefault("yml-path", ""),
    getOrDefault("working-directory", "./"),
    getOrDefault("output-pattern", outputPattern),
    getOrDefault("expected-output-code", "0").toInt(),
    getPropertyAsList("run-params", runParams),
)
