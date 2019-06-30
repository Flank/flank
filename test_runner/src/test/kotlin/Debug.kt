import ftl.Main
import picocli.CommandLine

fun main() {
    // GoogleApiLogger.logAllToStdout()

    // for debugging. Run test from IntelliJ IDEA
    CommandLine(Main()).execute("firebase", "test", "android", "run")
}
