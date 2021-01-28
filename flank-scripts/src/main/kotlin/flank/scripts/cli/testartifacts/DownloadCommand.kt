package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.downloadFixtures

object DownloadCommand : CliktCommand(
    help = "Download test artifacts zip asset to test_artifacts directory."
) {
    val artifacts by requireObject<Context>()
    val overwrite by option(
        "--overwrite", "-o",
        help = "Flag which indicates if should overwrite old resources when downloading"
    ).flag()

    override fun run() {
        artifacts.downloadFixtures(overwrite)
    }
}
