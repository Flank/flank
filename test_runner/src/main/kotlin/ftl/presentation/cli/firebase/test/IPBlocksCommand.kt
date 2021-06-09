package ftl.presentation.cli.firebase.test

import ftl.presentation.cli.firebase.test.ipblocks.IPBlocksListCommand
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "ip-blocks",
    synopsisHeading = "",
    subcommands = [IPBlocksListCommand::class],
    header = ["Explore IP blocks used by Firebase Test Lab devices."],
    usageHelpAutoWidth = true
)
class IPBlocksCommand : PrintHelp()
