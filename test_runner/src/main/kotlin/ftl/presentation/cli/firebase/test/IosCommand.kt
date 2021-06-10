package ftl.presentation.cli.firebase.test

import ftl.presentation.cli.firebase.test.ios.IosDoctorCommand
import ftl.presentation.cli.firebase.test.ios.IosLocalesCommand
import ftl.presentation.cli.firebase.test.ios.IosModelsCommand
import ftl.presentation.cli.firebase.test.ios.IosOrientationsCommand
import ftl.presentation.cli.firebase.test.ios.IosRunCommand
import ftl.presentation.cli.firebase.test.ios.IosTestEnvironmentCommand
import ftl.presentation.cli.firebase.test.ios.IosVersionsCommand
import ftl.util.PrintHelpCommand
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
class IosCommand : PrintHelpCommand()
