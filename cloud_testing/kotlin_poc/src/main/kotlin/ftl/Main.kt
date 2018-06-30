package ftl

import ftl.cli.FirebaseCommand
import picocli.CommandLine


@CommandLine.Command(
        name = "flank.jar\n",
        synopsisHeading = "",
        subcommands = [
            FirebaseCommand::class
        ]
)
object Main : Runnable {
    override fun run() {
        if (version) {
            println("v0.0.4")
        } else {
            CommandLine.usage(Main, System.out)
        }
    }

    @CommandLine.Option(names = ["-v", "--version"], description = ["Prints the version"])
    private var version = false

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
//        val args = arrayOf("firebase", "doctor") // for debugging. run test from IntelliJ IDEA
        CommandLine.run<Runnable>(Main, System.out, *args)
    }
}
