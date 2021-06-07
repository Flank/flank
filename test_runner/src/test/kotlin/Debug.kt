@file:Suppress("InvalidPackageDeclaration")

import ftl.presentation.cli.MainCommand
import ftl.reports.startDurationMeasurement
import ftl.run.exception.withGlobalExceptionHandling
import ftl.util.disableCrashReporting
import picocli.CommandLine
import java.io.File

fun main() {
    println(File("").absolutePath)
    disableCrashReporting()
    // GoogleApiLogger.logAllToStdout()

    // for debugging. Run test from IntelliJ IDEA

    // run "gradle check" to generate required fixtures
    val projectId = System.getenv("GOOGLE_CLOUD_PROJECT")
        ?: "YOUR PROJECT ID"

    withGlobalExceptionHandling {
        startDurationMeasurement()
        CommandLine(MainCommand()).execute(
            "firebase",
            "test",
            "ios",
            "run",
//            "--debug",
//            "--dry",
//            "--dump-shards",
//           "--output-style=single",
//            "--full-junit-result",
//            "--legacy-junit-result",
            "-c=./integration_tests/src/test/resources/cases/all_test_filtered_ios.yml",
            "--project=$projectId"
//            "--client-details=key1=value1,key2=value2"
        )
    }
}
