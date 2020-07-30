package flank.scripts.release.jfrog.sync

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.release.jfrog.flankMaven
import flank.scripts.utils.runCommand
import kotlin.system.exitProcess

class SyncMavenCommand : CliktCommand(name = "jFrogSync", help = "Sync maven repository using jfrog") {

    private val gitTag by option(help = "Git Tag").required()

    override fun run() {
        exitProcess(jFrogSync(gitTag))
    }
}

fun jFrogSync(gitTag: String) = "jfrog bt mcs ${flankMaven(gitTag)}".runCommand(retryCount = 3)
