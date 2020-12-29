@file:Suppress("InvalidPackageDeclaration")

import ftl.Main
import ftl.run.exception.withGlobalExceptionHandling
import picocli.CommandLine
import java.io.File

fun main() {
    println(File("").absolutePath)
    // GoogleApiLogger.logAllToStdout()

    // for debugging. Run test from IntelliJ IDEA

    // run "gradle check" to generate required fixtures
    val projectId = System.getenv("GOOGLE_CLOUD_PROJECT")
        ?: "YOUR PROJECT ID"

    // Bugsnag keeps the process alive so we must call exitProcess
    // https://github.com/bugsnag/bugsnag-java/issues/151
    withGlobalExceptionHandling {
        CommandLine(Main()).execute(
//            "--debug",
            "firebase",
            "test",
            "ios",
            "run",
//            "--dry",
//            "--dump-shards",
            "--output-style=single",
//            "--full-junit-result",
//            "--legacy-junit-result",
            "-c=./test_runner/src/test/kotlin/ftl/fixtures/test_app_cases/flank-single-gameloop-ios.yml",
            "--project=$projectId"
//            "--client-details=key1=value1,key2=value2"
        )
    }
}
