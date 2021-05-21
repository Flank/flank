package ftl.presentation.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.config.FtlConstants
import ftl.domain.RunDoctor
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.summary
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
        """Validates Android Flank YAML.
"""
    ],
    usageHelpAutoWidth = true
)
class AndroidDoctorCommand :
    Runnable,
    RunDoctor {

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
    override var configPath: String = FtlConstants.defaultAndroidConfig

    @Option(
        names = ["-f", "--fix"],
        description = ["Auto fix flank YAML file"]
    )
    override var fix: Boolean = false

    override fun run() = invoke(AndroidArgs)

    override val out = outputLogger {
        when (this) {
            is RunDoctor.Error -> summary()
            else -> throwUnknownType()
        }
    }
}
