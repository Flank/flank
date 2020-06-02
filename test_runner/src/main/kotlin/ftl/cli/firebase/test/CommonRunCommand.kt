package ftl.cli.firebase.test

import picocli.CommandLine

abstract class CommonRunCommand {

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    // Flank debug
    @CommandLine.Option(names = ["--dry"], description = ["Dry run on mock server"])
    var dryRun: Boolean = false

    // Flank specific

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var configPath: String = ""
}
