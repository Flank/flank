package ftl.cli

import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "android",
        synopsisHeading = "",
        subcommands = [
            AndroidRunCommand::class,
            AndroidRefreshCommand::class
        ])
class AndroidCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidCommand(), System.out)
    }
}