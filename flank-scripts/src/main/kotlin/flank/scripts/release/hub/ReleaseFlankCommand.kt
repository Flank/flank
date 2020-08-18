package flank.scripts.release.hub

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import flank.scripts.utils.ERROR_WHEN_RUNNING
import kotlin.system.exitProcess

class ReleaseFlankCommand : CliktCommand(name = "releaseFlank", help = "Release Flank on GitHub") {

    private val inputFile by option(help = "Path to release file").path(mustExist = true).required()
    private val snapshot by option(help = "Is Snapshot release").flag(default = false)
    private val gitTag by option(help = "Git Tag").required()
    private val commitHash by option(help = "Git Commit hash").default("")
    private val token by option(help = "Git Token").default("")

    override fun run() {
        when {
            snapshot && commitHash.isBlank() -> {
                println("Commit hash is required for snapshot release")
                exitProcess(ERROR_WHEN_RUNNING)
            }
            !snapshot && token.isBlank() -> {
                println("Git hub token is required for stable release")
                exitProcess(ERROR_WHEN_RUNNING)
            }
            else -> exitProcess(releaseFlank(inputFile, gitTag, commitHash, snapshot, token))
        }
    }
}
