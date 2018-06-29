package ftl.cli

import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "ios",
        synopsisHeading = "",
        subcommands = [
            IosRunCommand::class,
            IosRefreshCommand::class
        ])
class IosCommand : Runnable {
    override fun run() {
        CommandLine.usage(IosCommand(), System.out)
    }
}