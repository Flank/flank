package flank.scripts.release.jfrog.sync

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.release.jfrog.flankMaven
import flank.scripts.utils.runCommand
import kotlin.system.exitProcess

class SyncMavenCommand : CliktCommand(name = "jFrogSync", help = "Sync maven repository using jfrog") {

    private val mavenTag by option(help = "Maven Tag").required()

    override fun run() {
        exitProcess(jFrogSync(mavenTag))
    }
}

fun jFrogSync(mavenTag: String) = "jfrog bt mcs ${flankMaven(mavenTag)}".runCommand(retryCount = 5)
