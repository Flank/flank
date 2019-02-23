import ftl.Main
import picocli.CommandLine

fun main() {
    // GoogleApiLogger.logAllToStdout()

    val arguments = arrayOf("firebase", "test", "android", "run") // for debugging. run test from IntelliJ IDEA
    CommandLine.run<Runnable>(Main(), System.out, *arguments)
}
