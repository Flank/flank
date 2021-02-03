package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.zipTestArtifacts

object ZipCommand : CliktCommand(
    help = "Create zip archive from test artifacts directory."
) {
    val artifacts by requireObject<Context>()

    override fun run() {
        artifacts.zipTestArtifacts()
    }
}
