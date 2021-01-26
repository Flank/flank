package flank.scripts.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.shell.firebase.apiclient.generateJavaClient
import flank.scripts.ops.shell.firebase.apiclient.updateApiJson
import flank.scripts.ops.shell.firebase.sdk.checkForSDKUpdate

object FirebaseCommand : CliktCommand(name = "firebase", help = "Contains all firebase commands") {

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

object UpdateApiJsonCommand : CliktCommand(name = "updateApiJson", help = "Download file for generating client") {
    override fun run() {
        updateApiJson()
    }
}

object GenerateJavaClientCommand : CliktCommand(name = "generateJavaClient", help = "Generate Java Client") {

    override fun run() {
        generateJavaClient()
    }
}

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
