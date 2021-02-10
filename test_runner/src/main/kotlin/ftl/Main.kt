package ftl

import ftl.cli.AuthCommand
import ftl.cli.FirebaseCommand
import ftl.cli.firebase.CancelCommand
import ftl.cli.firebase.RefreshCommand
import ftl.cli.firebase.test.AndroidCommand
import ftl.cli.firebase.test.IPBlocksCommand
import ftl.cli.firebase.test.IosCommand
import ftl.cli.firebase.test.NetworkProfilesCommand
import ftl.cli.firebase.test.ProvidedSoftwareCommand
import ftl.log.setDebugLogging
import ftl.run.exception.withGlobalExceptionHandling
import ftl.util.printVersionInfo
import picocli.CommandLine

@CommandLine.Command(
    name = "flank.jar\n",
    synopsisHeading = "",
    subcommands = [
        FirebaseCommand::class,
        IosCommand::class,
        AndroidCommand::class,
        RefreshCommand::class,
        CancelCommand::class,
        AuthCommand::class,
        ProvidedSoftwareCommand::class,
        NetworkProfilesCommand::class,
        IPBlocksCommand::class
    ]
)
class Main : Runnable {
    override fun run() {
        if (printVersion) printVersionInfo()
        else CommandLine.usage(Main::class.java, System.out)
    }

    @CommandLine.Option(names = ["-v", "--version"], description = ["Prints the version"])
    private var printVersion = false

    @CommandLine.Option(
        names = ["--debug"],
        description = ["Enables debug logging"],
        defaultValue = "false"
    )
    fun debug(enabled: Boolean) = setDebugLogging(enabled)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            withGlobalExceptionHandling {
                CommandLine(Main()).execute(*args)
            }
        }
    }
}
