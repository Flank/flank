package ftl.cli

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "android",
        synopsisHeading = "",
        subcommands = [
            AndroidRunCommand::class,
            AndroidRefreshCommand::class
        ])
class AndroidCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidCommand, System.out)
    }

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            AndroidCommand().run()
        }
    }
}