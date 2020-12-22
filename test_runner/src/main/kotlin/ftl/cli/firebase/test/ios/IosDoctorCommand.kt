package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.cli.firebase.test.processValidation
import ftl.config.FtlConstants
import ftl.doctor.validateYaml
import java.nio.file.Paths
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "doctor",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Verifies flank firebase is setup correctly"],
    description = [
        """Validates iOS YAML and connection to iOS catalog.
"""
    ],
    usageHelpAutoWidth = true
)
class IosDoctorCommand : Runnable {
    override fun run() {
        val ymlPath = Paths.get(configPath)
        val validationResult = validateYaml(IosArgs, Paths.get(configPath))
        processValidation(validationResult, fix, ymlPath)
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultIosConfig

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    @Option(names = ["-f", "--fix"], description = ["Auto fix flank YAML file"])
    var fix: Boolean = false
}
