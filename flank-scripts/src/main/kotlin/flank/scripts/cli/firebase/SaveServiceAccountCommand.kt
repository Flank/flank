package flank.scripts.cli.firebase

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.firebase.saveServiceAccount

object SaveServiceAccountCommand : CliktCommand(
    name = "save_service_account",
    help = ""
) {
    private val serviceAccount: String by option("--account").required()
    override fun run() {
        saveServiceAccount(serviceAccount)
    }
}
