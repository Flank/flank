package ftl.cli.firebase.test

import ftl.cli.firebase.test.android.*
import ftl.util.PrintHelp
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
class AndroidCommand : PrintHelp
