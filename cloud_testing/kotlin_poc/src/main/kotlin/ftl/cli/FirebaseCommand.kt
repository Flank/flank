package ftl.cli

import ftl.cli.firebase.test.ios.IosDoctorCommand
import ftl.cli.firebase.TestCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "firebase",
        synopsisHeading = "",
        subcommands = [
            TestCommand::class,
            IosDoctorCommand::class
        ])
class FirebaseCommand : Runnable {
    override fun run() {
        CommandLine.usage(FirebaseCommand(), System.out)
    }
}
