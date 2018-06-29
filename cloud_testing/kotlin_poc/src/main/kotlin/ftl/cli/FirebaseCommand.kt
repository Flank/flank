package ftl.cli

import picocli.CommandLine
import picocli.CommandLine.Command

@Command(name = "firebase",
        synopsisHeading = "",
        subcommands = [
            TestCommand::class,
            DoctorCommand::class
        ])
class FirebaseCommand : Runnable {
    override fun run() {
        CommandLine.usage(FirebaseCommand(), System.out)
    }
}