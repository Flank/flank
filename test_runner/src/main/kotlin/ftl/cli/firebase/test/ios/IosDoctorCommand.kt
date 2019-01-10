package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.doctor.Doctor.checkIosCatalog
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
    description = ["""Validates iOS YAML and connection to iOS catalog.
"""]
)
class IosDoctorCommand : Runnable {
    override fun run() {
        checkIosCatalog()
        println(validateYaml(IosArgs, Paths.get(configPath)))
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = "./flank.ios.yml"

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
