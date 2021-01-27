package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.ops.go.generateGoArtifacts

object GoOpsCommand : CliktCommand(name = "go", help = "Build go app with tests") {
    override fun run() {
        generateGoArtifacts()
    }
}
