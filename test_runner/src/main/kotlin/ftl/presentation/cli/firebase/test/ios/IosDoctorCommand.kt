package ftl.presentation.cli.firebase.test.ios

import ftl.config.FtlConstants
import ftl.domain.DoctorErrors
import ftl.domain.RunDoctorIos
import ftl.domain.invoke
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
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
class IosDoctorCommand :
    Runnable,
    RunDoctorIos {

    @Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    @Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultIosConfig

    @Option(
        names = ["-f", "--fix"],
        description = ["Auto fix flank YAML file"]
    )
    override var fix: Boolean = false

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is DoctorErrors -> summary()
            else -> throwUnknownType()
        }
    }
}
