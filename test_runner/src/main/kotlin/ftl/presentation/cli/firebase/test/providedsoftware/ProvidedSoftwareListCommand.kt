package ftl.presentation.cli.firebase.test.providedsoftware

import ftl.domain.ListProvidedSoftware
import ftl.domain.invoke
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["The software environment provided by TestExecutionService."],
    usageHelpAutoWidth = true
)
class ProvidedSoftwareListCommand :
    Runnable,
    ListProvidedSoftware {
    override fun run() = invoke()
}
