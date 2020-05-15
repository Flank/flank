package ftl.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.cli.firebase.test.processValidation
import ftl.doctor.Doctor.validateYaml
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
    description = ["""Validates Android Flank YAML.
"""],
    usageHelpAutoWidth = true
)
class AndroidDoctorCommand : Runnable {
    override fun run() {
        val ymlPath = Paths.get(configPath)
        val validationResult = validateYaml(AndroidArgs, ymlPath)
        processValidation(validationResult, fix, ymlPath)
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = "./flank.yml"

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    @Option(names = ["-f", "--fix"], description = ["Auto fix flank YAML file"])
    var fix: Boolean = false
}
