package ftl

import ftl.cli.FirebaseCommand
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
        AndroidCommand::class
    ]
)
object Main : Runnable {
    override fun run() {
        if (printVersion) {
            println(readTextResource("version.txt").trim())
        } else {
            CommandLine.usage(Main, System.out)
        }
    }

    @CommandLine.Option(names = ["-v", "--version"], description = ["Prints the version"])
    private var printVersion = false

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // val args = arrayOf("firebase", "test", "android", "run") // for debugging. run test from IntelliJ IDEA
        CommandLine.run<Runnable>(Main, System.out, *args)
    }
}
