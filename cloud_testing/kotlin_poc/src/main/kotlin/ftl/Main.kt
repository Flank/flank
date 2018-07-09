package ftl

import ftl.cli.AuthCommand
import ftl.cli.DoctorCommand
import ftl.cli.RefreshCommand
import ftl.cli.RunCommand
import picocli.CommandLine


@CommandLine.Command(
        name = "flank.jar\n",
        synopsisHeading = "",
        subcommands = [
            RunCommand::class,
            RefreshCommand::class,
            DoctorCommand::class,
            AuthCommand::class
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
        val args = arrayOf("doctor") // for debugging. run test from IntelliJ IDEA
        CommandLine.run<Runnable>(Main, System.out, *args)

//        GoogleApiLogger.logAllToStdout()
//        // X-Goog-User-Project => get quota project.
//        val project = ServiceOptions.getDefaultProjectId()
//        val cred = CredTmp.authorize() // Credential
//        val cred2 = GoogleCredential.getApplicationDefault() // GoogleCredential // serviceAccountProjectId
//
//        DoctorCommand().run()
    }
}
