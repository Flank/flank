package ftl.cli.firebase.test

import ftl.cli.firebase.test.networkprofiles.NetworkProfilesDescribeCommand
import ftl.cli.firebase.test.networkprofiles.NetworkProfilesListCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "network-profiles",
    synopsisHeading = "",
    subcommands = [
        NetworkProfilesListCommand::class,
        NetworkProfilesDescribeCommand::class
    ],
    usageHelpAutoWidth = true
)
class NetworkProfilesCommand : Runnable {
    override fun run() {
        CommandLine.usage(NetworkProfilesCommand(), System.out)
    }
}
