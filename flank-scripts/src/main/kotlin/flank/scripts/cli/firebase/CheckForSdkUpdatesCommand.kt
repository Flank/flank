package flank.scripts.cli.firebase

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.shell.firebase.sdk.checkForSDKUpdate

object CheckForSdkUpdatesCommand : CliktCommand(
    name = "check_for_sdk_updates",
    help = "Check for new SDK features and create update tasks for it"
) {
    private val githubToken by option(help = "Git Token").required()
    private val zenhubToken by option(help = "Zenhub Token").required()

    override fun run() {
        checkForSDKUpdate(githubToken, zenhubToken)
    }
}
