package flank.scripts.release.hub

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path

class ReleaseFlankCommand : CliktCommand(name = "releaseFlank", help = "Release Flank on GitHub") {

    private val inputFile by option(help = "Path to release file").path(mustExist = true).required()
    private val snapshot by option(help = "Is Snapshot release").flag(default = false)
    private val gitTag by option( help = "Git Tag").required()
    private val commitHash by option( help = "Git Commit hash").required()

    override fun run() {
        releaseFlank(inputFile, gitTag, commitHash, snapshot)
    }
}
