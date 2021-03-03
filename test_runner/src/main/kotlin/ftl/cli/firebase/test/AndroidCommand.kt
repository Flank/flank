package ftl.cli.firebase.test

import ftl.cli.firebase.test.android.AndroidDoctorCommand
import ftl.cli.firebase.test.android.AndroidLocalesCommand
import ftl.cli.firebase.test.android.AndroidModelsCommand
import ftl.cli.firebase.test.android.AndroidOrientationsCommand
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.cli.firebase.test.android.AndroidTestEnvironmentCommand
import ftl.cli.firebase.test.android.AndroidVersionsCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "android",
    synopsisHeading = "",
    subcommands = [
        AndroidRunCommand::class,
        AndroidDoctorCommand::class,
        AndroidModelsCommand::class,
        AndroidVersionsCommand::class,
        AndroidOrientationsCommand::class,
        AndroidLocalesCommand::class,
        AndroidTestEnvironmentCommand::class
    ],
    usageHelpAutoWidth = true
)
class AndroidCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidCommand(), System.out)
    }
}
