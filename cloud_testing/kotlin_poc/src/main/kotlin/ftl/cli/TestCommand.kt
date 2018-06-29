package ftl.cli

import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "test",
        synopsisHeading = "",
        subcommands = [
            AndroidCommand::class,
            IosCommand::class
        ])
class TestCommand : Runnable {
    override fun run() {
        CommandLine.usage(TestCommand(), System.out)
    }
}