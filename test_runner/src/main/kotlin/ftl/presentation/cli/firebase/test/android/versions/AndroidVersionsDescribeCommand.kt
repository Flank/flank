package ftl.presentation.cli.firebase.test.android.versions

import ftl.config.FtlConstants
import ftl.domain.DescribeAndroidVersions
import ftl.domain.invoke
import picocli.CommandLine

@CommandLine.Command(
    name = "describe",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["List of OS versions available to test against"],
    description = ["Print current list of android versions available to test against"],
    usageHelpAutoWidth = true
)
class AndroidVersionsDescribeCommand :
    Runnable,
    DescribeAndroidVersions {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultAndroidConfig

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "VERSION_ID",
        defaultValue = "",
        description = [
            "The version to describe, found" +
                " using \$ gcloud firebase test android versions list."
        ]
    )
    override var versionId: String = ""

    override fun run() = invoke()
}
