package ftl.cli

import ftl.ios.IosCatalog
import kotlinx.coroutines.experimental.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "doctor",
        sortOptions = false,
        headerHeading = "",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = ["Verifies flank is setup correctly"],
        description = ["""Fetches the iOS catalog to verify Flank auth is valid.
"""])
class DoctorCommand : Runnable {
    override fun run() {
        runBlocking {
            if (!IosCatalog.supported("iphone8", "11.2")) {
                throw RuntimeException("iPhone 8 with iOS 11.2 is unexpectedly unsupported")
            }
            println("Flank successfully connected to iOS catalog")
        }
    }

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
