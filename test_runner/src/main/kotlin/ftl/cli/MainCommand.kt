package ftl.cli

import ftl.cli.firebase.CancelCommand
import ftl.cli.firebase.RefreshCommand
import ftl.cli.firebase.test.AndroidCommand
import ftl.cli.firebase.test.IPBlocksCommand
import ftl.cli.firebase.test.IosCommand
import ftl.cli.firebase.test.NetworkProfilesCommand
import ftl.cli.firebase.test.ProvidedSoftwareCommand
import ftl.log.setDebugLogging
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
class MainCommand : Runnable {

    @CommandLine.Option(
        names = ["-v", "--version"],
        description = ["Prints the version"]
    )
    private var printVersion = false

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
