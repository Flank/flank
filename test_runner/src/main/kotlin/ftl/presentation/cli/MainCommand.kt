package ftl.presentation.cli

import ftl.log.setDebugLogging
import ftl.presentation.cli.firebase.CancelCommand
import ftl.presentation.cli.firebase.RefreshCommand
import ftl.presentation.cli.firebase.test.AndroidCommand
import ftl.presentation.cli.firebase.test.IPBlocksCommand
import ftl.presentation.cli.firebase.test.IosCommand
import ftl.presentation.cli.firebase.test.NetworkProfilesCommand
import ftl.presentation.cli.firebase.test.ProvidedSoftwareCommand
import ftl.util.printVersionInfo
import picocli.CommandLine

@CommandLine.Command(
    name = "flank.jar\n",
    synopsisHeading = "",
    subcommands = [
        FirebaseCommand::class,
        CorelliumCommand::class,
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
class MainCommand : Runnable {

    @CommandLine.Option(
        names = ["-v", "--version"],
        description = ["Prints the version"]
    )
    private var printVersion = false

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    @CommandLine.Option(
        names = ["--debug"],
        description = ["Enables debug logging"],
        defaultValue = "false"
    )
    fun debug(enabled: Boolean) = setDebugLogging(enabled)

    override fun run() {
        if (printVersion) printVersionInfo()
        else CommandLine.usage(this, System.out)
    }
}
