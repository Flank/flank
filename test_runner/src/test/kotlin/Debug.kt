import ftl.Main
import ftl.util.withGlobalExceptionHandling
import picocli.CommandLine

//fun main() {
//    // GoogleApiLogger.logAllToStdout()
//
//    // for debugging. Run test from IntelliJ IDEA
//
//    // run "gradle check" to generate required fixtures
//    val projectId = System.getenv("GOOGLE_CLOUD_PROJECT")
//        ?: "YOUR PROJECT ID"
//    val quantity = "single"
//    val type = "robo"
//
//    // Bugsnag keeps the process alive so we must call exitProcess
//    // https://github.com/bugsnag/bugsnag-java/issues/151
//    withGlobalExceptionHandling {
//        CommandLine(Main()).execute(
////            "--debug",
//            "firebase", "test",
//            "android", "run",
////            "--dry",
//            "-c=src/test/kotlin/ftl/fixtures/test_app_cases/flank-$quantity-$type.yml",
//            "--project=$projectId"
////            "--client-details=key1=value1,key2=value2"
//        )
//    }
//}


fun main() {
    // GoogleApiLogger.logAllToStdout()

    // for debugging. Run test from IntelliJ IDEA

    // run "gradle check" to generate required fixtures
//    val projectId = System.getenv("GOOGLE_CLOUD_PROJECT")
//        ?: "YOUR PROJECT ID"
//    val quantity = "single"
//    val type = "robo"

    // Bugsnag keeps the process alive so we must call exitProcess
    // https://github.com/bugsnag/bugsnag-java/issues/151
    withGlobalExceptionHandling {
        CommandLine(Main()).execute(
            "hypershard",
            "android",
            "--dirs=/Users/gogo/flank/test_runner/src/test/kotlin"
        )
    }
}
