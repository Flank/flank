import ftl.Main
import picocli.CommandLine

object Debug {

    @JvmStatic
    @Suppress("UnusedPrivateMember") // Suppress detekt rule
    fun main(args: Array<String>) {
        // GoogleApiLogger.logAllToStdout()

        val arguments = arrayOf("firebase", "test", "android", "run") // for debugging. run test from IntelliJ IDEA
        CommandLine.run<Runnable>(Main(), System.out, *arguments)
    }
}
