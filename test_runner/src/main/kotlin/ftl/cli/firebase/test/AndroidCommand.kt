package ftl.cli.firebase.test

import ftl.cli.firebase.test.android.AndroidDoctorCommand
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.cli.firebase.test.android.AndroidTestEnvironmentCommand
import ftl.cli.firebase.test.android.models.AndroidModelsCommand
import ftl.cli.firebase.test.android.versions.AndroidVersionsCommand
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
        AndroidTestEnvironmentCommand::class
    ],
    usageHelpAutoWidth = true
)
class AndroidCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidCommand(), System.out)
    }
}
