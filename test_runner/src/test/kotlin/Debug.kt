import ftl.Main
import picocli.CommandLine
import kotlin.system.exitProcess

fun main() {
    // GoogleApiLogger.logAllToStdout()

    // for debugging. Run test from IntelliJ IDEA

    // run "gradle check" to generate required fixtures
    val projectId = System.getenv("FLANK_PROJECT_ID")
        ?: "YOUR PROJECT ID"
    val quantity = "single"
    val type = "success"

    exitProcess(CommandLine(Main()).execute(
        "--debug",
        "firebase", "test",
        "android", "run",
        "--dry",
        "-c=src/test/kotlin/ftl/fixtures/test_app_cases/flank-$quantity-$type.yml",
        "--project=$projectId"
    ))
}
