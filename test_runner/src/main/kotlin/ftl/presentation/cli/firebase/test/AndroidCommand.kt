package ftl.presentation.cli.firebase.test

import ftl.presentation.cli.firebase.test.android.AndroidDoctorCommand
import ftl.presentation.cli.firebase.test.android.AndroidLocalesCommand
import ftl.presentation.cli.firebase.test.android.AndroidModelsCommand
import ftl.presentation.cli.firebase.test.android.AndroidOrientationsCommand
import ftl.presentation.cli.firebase.test.android.AndroidRunCommand
import ftl.presentation.cli.firebase.test.android.AndroidTestEnvironmentCommand
import ftl.presentation.cli.firebase.test.android.AndroidVersionsCommand
import ftl.util.PrintHelp
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
class AndroidCommand : PrintHelp {
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
