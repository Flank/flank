package ftl.cli.firebase.test

import ftl.cli.firebase.test.ios.*
import ftl.util.PrintHelp
import picocli.CommandLine.Command

@Command(
    name = "ios",
    synopsisHeading = "",
    subcommands = [
        IosRunCommand::class,
        IosDoctorCommand::class,
        IosModelsCommand::class,
        IosVersionsCommand::class,
        IosLocalesCommand::class,
        IosTestEnvironmentCommand::class,
        IosOrientationsCommand::class
    ],
    usageHelpAutoWidth = true
)
class IosCommand : PrintHelp
