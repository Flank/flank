package flank.scripts.cli.firebase

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object FirebaseCommand : CliktCommand(
    name = "firebase",
    help = "Group of commands for managing firebase integrations"
) {
    init {
        subcommands(
            CheckForSdkUpdatesCommand,
            SaveServiceAccountCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
