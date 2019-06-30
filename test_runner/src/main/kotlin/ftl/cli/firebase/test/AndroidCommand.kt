package ftl.cli.firebase.test

import ftl.cli.firebase.test.android.AndroidDoctorCommand
import ftl.cli.firebase.test.android.AndroidRunCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "android",
    synopsisHeading = "",
    subcommands = [
        AndroidRunCommand::class,
        AndroidDoctorCommand::class
    ],
    usageHelpAutoWidth = true
)
class AndroidCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidCommand(), System.out)
    }
}
