package ftl.presentation.cli.firebase.test

import ftl.presentation.cli.firebase.test.networkprofiles.NetworkProfilesDescribeCommand
import ftl.presentation.cli.firebase.test.networkprofiles.NetworkProfilesListCommand
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "network-profiles",
    synopsisHeading = "",
    subcommands = [
        NetworkProfilesListCommand::class,
        NetworkProfilesDescribeCommand::class
    ],
    header = ["Explore network profiles available for testing."],
    usageHelpAutoWidth = true
)
class NetworkProfilesCommand : PrintHelp()
