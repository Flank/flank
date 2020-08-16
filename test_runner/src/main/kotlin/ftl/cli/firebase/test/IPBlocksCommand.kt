package ftl.cli.firebase.test

import ftl.cli.firebase.test.ipblocks.IPBlocksListCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "ip-blocks",
    synopsisHeading = "",
    subcommands = [IPBlocksListCommand::class],
    header = ["Explore IP blocks used by Firebase Test Lab devices."],
    usageHelpAutoWidth = true
)
class IPBlocksCommand : Runnable {
    override fun run() {
        CommandLine.usage(IPBlocksCommand(), System.out)
    }
}
