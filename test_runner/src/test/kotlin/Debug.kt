import ftl.Main
import picocli.CommandLine

fun main() {
    // GoogleApiLogger.logAllToStdout()

    // for debugging. Run test from IntelliJ IDEA

    // run "gradle check" to generate required fixtures
    val projectId = System.getenv("FLANK_PROJECT_ID")
        ?: "YOUR PROJECT ID"
    val quantity = "multiple"
    val type = "success"

    CommandLine(Main()).execute(
        "firebase", "test",
        "android", "run",
        "-c=src/test/kotlin/ftl/fixtures/test_app_cases/flank-$quantity-$type.yml",
        "--project=$projectId"
    )
}
