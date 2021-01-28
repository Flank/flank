package flank.scripts.cli.shell.firebase

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.firebase.apiclient.updateApiJson

object UpdateApiJsonCommand : CliktCommand(name = "updateApiJson", help = "Download file for generating client") {
    override fun run() {
        updateApiJson()
    }
}
