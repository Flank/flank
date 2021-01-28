package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.uploadFixtures

object UploadCommand : CliktCommand(
    help = "Upload test artifacts zip as github release asset."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.uploadFixtures()
    }
}
