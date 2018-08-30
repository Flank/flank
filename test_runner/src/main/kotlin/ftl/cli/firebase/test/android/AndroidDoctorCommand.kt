package ftl.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.doctor.Doctor.validateYaml
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Paths

@Command(name = "doctor",
        sortOptions = false,
        headerHeading = "",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = ["Verifies flank firebase is setup correctly"],
        description = ["""Validates Android Flank YAML.
"""])
class AndroidDoctorCommand : Runnable {
    override fun run() {
        println(validateYaml(AndroidArgs, Paths.get(configPath)))
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = "./flank.yml"

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
