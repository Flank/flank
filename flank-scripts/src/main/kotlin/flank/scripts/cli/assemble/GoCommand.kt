package flank.scripts.cli.assemble

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.buildexample.go.generateGoArtifacts

object GoCommand : CliktCommand(
    name = "go",
    help = "Generate go artifacts"
) {
    override fun run() {
        generateGoArtifacts()
    }
}
