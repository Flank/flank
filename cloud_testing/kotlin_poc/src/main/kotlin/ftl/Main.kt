package ftl

import ftl.cli.RefreshCommand
import ftl.cli.RunCommand
import picocli.CommandLine

@CommandLine.Command(
        name = "flank.jar\n",
        synopsisHeading = "",
        subcommands = [
            RunCommand::class,
            RefreshCommand::class
        ]
)
object Main : Runnable {
    override fun run() {
        CommandLine.usage(Main, System.out);
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        CommandLine.run<Runnable>(Main, System.out, *args)
    }
}
