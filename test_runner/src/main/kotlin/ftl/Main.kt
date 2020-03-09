package ftl

import ftl.cli.AuthCommand
import ftl.cli.FirebaseCommand
import ftl.cli.firebase.CancelCommand
import ftl.cli.firebase.RefreshCommand
import ftl.cli.firebase.test.AndroidCommand
import ftl.cli.firebase.test.IosCommand
import ftl.log.setDebugLogging
import ftl.util.readRevision
import ftl.util.readVersion
import picocli.CommandLine
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "flank.jar\n",
    synopsisHeading = "",
    subcommands = [
        FirebaseCommand::class,
        IosCommand::class,
        AndroidCommand::class,
        RefreshCommand::class,
        CancelCommand::class,
        AuthCommand::class
    ]
)
class Main : Runnable {
    override fun run() {
        if (printVersion) {
            println(readVersion())
            println(readRevision())
        } else {
            CommandLine.usage(Main::class.java, System.out)
        }
    }

    @CommandLine.Option(names = ["-v", "--version"], description = ["Prints the version"])
    private var printVersion = false

    @CommandLine.Option(
        names = ["--debug"],
        description = ["Enables debug logging"]
    )
    fun debug(enabled: Boolean) = setDebugLogging(enabled)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // BugSnag opens a non-daemon thread which will keep the JVM process alive.
            // Flank must invoke exitProcess to exit cleanly.
            // https://github.com/bugsnag/bugsnag-java/issues/151
            try {
                exitProcess(CommandLine(Main()).execute(*args))
            } catch (t: Throwable) {
                t.printStackTrace()
                exitProcess(CommandLine.ExitCode.SOFTWARE)
            }
        }
    }
}
