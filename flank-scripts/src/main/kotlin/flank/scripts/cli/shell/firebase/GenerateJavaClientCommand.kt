package flank.scripts.cli.shell.firebase

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.firebase.apiclient.generateJavaClient

object GenerateJavaClientCommand : CliktCommand(name = "generateJavaClient", help = "Generate Java Client") {

    override fun run() {
        generateJavaClient()
    }
}
