package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.unzipTestArtifacts

object UnzipCommand : CliktCommand(
    help = "Unpack test artifacts zip archive."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.unzipTestArtifacts()
    }
}
