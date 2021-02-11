package flank.scripts.cli.dependencies

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.dependencies.setupIosEnv

object SetupIosEnvCommand : CliktCommand(
    name = "setup_ios_env",
    help = "Setup iOS environment"
) {
    override fun run() {
        setupIosEnv()
    }
}
