package flank.scripts.cli.shell.firebase

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.shell.firebase.sdk.checkForSDKUpdate

object CheckForSDKUpdateCommand : CliktCommand(
    name = "checkForSdkUpdate",
    help = "Verifies if there were changes in gcloud sdk that need to be implemented in flank"
) {

    private val githubToken by option(help = "Git Token").required()
    private val zenhubToken by option(help = "Zenhub Token").required()

    override fun run() {
        checkForSDKUpdate(githubToken, zenhubToken)
    }
}
