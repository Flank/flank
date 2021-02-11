package flank.scripts.cli.firebase

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.firebase.updateApiJson

object UpdateApiCommand : CliktCommand(
    name = "update_api",
    help = "Update api schema"
) {
    override fun run() {
        updateApiJson()
    }
}
