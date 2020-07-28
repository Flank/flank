package flank.scripts.release.jfrog.sync

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class SyncMavenCommand : CliktCommand(name = "jFrogSync", help = "Sync maven repository using jfrog") {

    private val gitTag by option( help = "Git Tag").required()

    override fun run() {
        jFrogSync(gitTag)
    }
}
