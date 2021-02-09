package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.release.jfrog.jFrogSync
import kotlin.system.exitProcess

object SyncWithMavenCentralCommand : CliktCommand(
    name = "sync_with_maven_central",
    help = "Sync artifact's repository with Maven central"
) {
    private val mavenTag by option(help = "Maven Tag").required()

    override fun run() {
        exitProcess(jFrogSync(mavenTag))
    }
}
