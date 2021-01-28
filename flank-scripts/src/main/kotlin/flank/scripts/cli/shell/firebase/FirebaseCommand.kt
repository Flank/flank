package flank.scripts.cli.shell.firebase

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object FirebaseCommand : CliktCommand(
    name = "firebase",
    help = "Contains all firebase commands"
) {
    init {
        subcommands(
            UpdateApiJsonCommand,
            GenerateJavaClientCommand,
            CheckForSDKUpdateCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
