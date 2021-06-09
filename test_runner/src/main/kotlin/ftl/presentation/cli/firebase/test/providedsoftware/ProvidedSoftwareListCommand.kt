package ftl.presentation.cli.firebase.test.providedsoftware

import com.google.testing.model.ProvidedSoftwareCatalog
import ftl.domain.ListProvidedSoftware
import ftl.domain.invoke
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelpCommand
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
    PrintHelpCommand(),
    ListProvidedSoftware {

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is ProvidedSoftwareCatalog -> toCliTable()
            else -> throwUnknownType()
        }
    }
}
