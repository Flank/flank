package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.resolveArtifacts

object ResolveCommand : CliktCommand(
    help = "Automatically prepare local artifacts if needed."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.resolveArtifacts()
    }
}
