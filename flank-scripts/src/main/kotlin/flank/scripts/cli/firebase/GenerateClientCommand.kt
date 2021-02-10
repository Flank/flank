package flank.scripts.cli.firebase

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.firebase.generateJavaClient

object GenerateClientCommand : CliktCommand(
    name = "generate_client",
    help = "Generate Java Client based on api schema"
) {
    override fun run() {
        generateJavaClient()
    }
}
