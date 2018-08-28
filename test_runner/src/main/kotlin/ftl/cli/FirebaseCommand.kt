package ftl.cli

import ftl.cli.firebase.TestCommand
import ftl.cli.firebase.test.ios.IosDoctorCommand
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
