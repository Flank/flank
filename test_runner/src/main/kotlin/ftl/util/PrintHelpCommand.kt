package ftl.util

import picocli.CommandLine

abstract class PrintHelpCommand : Runnable {
    override fun run() {
        CommandLine.usage(this, System.out)
    }
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
