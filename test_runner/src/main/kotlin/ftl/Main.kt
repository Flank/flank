package ftl

import ftl.cli.AuthCommand
import ftl.cli.FirebaseCommand
import ftl.cli.firebase.CancelCommand
import ftl.cli.firebase.RefreshCommand
import ftl.cli.firebase.test.AndroidCommand
import ftl.cli.firebase.test.IosCommand
import ftl.util.Utils.readTextResource
import picocli.CommandLine

@CommandLine.Command(
    name = "flank.jar\n",
    synopsisHeading = "",
    subcommands = [
        FirebaseCommand::class,
        IosCommand::class,
        AndroidCommand::class,
        RefreshCommand::class,
        CancelCommand::class,
        AuthCommand::class
    ]
)
class Main : Runnable {
    override fun run() {
        if (printVersion) {
            // inline functions like .trim aren't counted as code coverage
            // https://github.com/jacoco/jacoco/issues/654
            // https://github.com/jacoco/jacoco/issues/754
            val version = readTextResource("version.txt").trim()
            println(version)
        } else {
            CommandLine.usage(Main::class.java, System.out)
        }
    }

    @CommandLine.Option(names = ["-v", "--version"], description = ["Prints the version"])
    private var printVersion = false

    companion object {
       @JvmStatic
        fun main(args: Array<String>) {
            CommandLine.run<Runnable>(Main(), System.out, *args)
        }
    }
}
