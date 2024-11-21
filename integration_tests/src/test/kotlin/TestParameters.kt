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
    listOf("firebase", "test", "ios", "run")
)

private fun Map<String, String>.withDefault(outputPattern: String, runParams: List<String>) = TestParameters(
    getOrValueIfEmpty("flank-path", ""),
    getOrValueIfEmpty("yml-path", ""),
    getOrValueIfEmpty("working-directory", "./"),
    getOrValueIfEmpty("output-pattern", outputPattern),
    getOrValueIfEmpty("expected-output-code", "0").toInt(),
    getPropertyAsList("run-params", runParams),
)

fun Map<String, String>.getOrValueIfEmpty(key: String, default: String) = get(key).orEmpty().let {
    if (it.isBlank()) default
    else it
}
