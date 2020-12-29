package flank.scripts.shell.firebase

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.shell.firebase.apiclient.GenerateJavaClientCommand
import flank.scripts.shell.firebase.apiclient.UpdateApiJsonCommand

object FirebaseCommand : CliktCommand(name = "firebase", help = "Contains all firebase commands") {

    init {
        subcommands(
            UpdateApiJsonCommand,
            GenerateJavaClientCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
