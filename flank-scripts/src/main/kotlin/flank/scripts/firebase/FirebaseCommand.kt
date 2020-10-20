package flank.scripts.firebase

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class FirebaseCommand : CliktCommand(name = "firebase", help = "Contains all firebase commands") {

    init {
        subcommands(
            GenerateJavaClient()
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
