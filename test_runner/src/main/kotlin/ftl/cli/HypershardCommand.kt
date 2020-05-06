package ftl.cli

import ftl.cli.hypershard.android.HyperAndroidCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "hypershard",
    synopsisHeading = "",
    subcommands = [
        HyperAndroidCommand::class
    ],
    usageHelpAutoWidth = true
)
class HypershardCommand : Runnable {
    override fun run() {
        CommandLine.usage(HypershardCommand(), System.out)
    }
}
