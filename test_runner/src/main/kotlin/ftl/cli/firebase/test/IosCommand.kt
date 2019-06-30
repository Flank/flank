package ftl.cli.firebase.test

import ftl.cli.firebase.test.ios.IosDoctorCommand
import ftl.cli.firebase.test.ios.IosRunCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "ios",
    synopsisHeading = "",
    subcommands = [
        IosRunCommand::class,
        IosDoctorCommand::class
    ],
    usageHelpAutoWidth = true
)
class IosCommand : Runnable {
    override fun run() {
        CommandLine.usage(IosCommand(), System.out)
    }
}
