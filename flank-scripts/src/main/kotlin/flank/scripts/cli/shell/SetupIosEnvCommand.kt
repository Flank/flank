package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.setupIosEnv

object SetupIosEnvCommand : CliktCommand(
    name = "setup_ios_env",
    help = "Build ios app with tests"
) {
    override fun run() {
        setupIosEnv()
    }
}
