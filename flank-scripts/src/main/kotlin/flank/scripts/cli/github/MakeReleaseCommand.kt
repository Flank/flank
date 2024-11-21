package flank.scripts.cli.github

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import flank.scripts.ops.github.tryReleaseFlank
import kotlin.system.exitProcess

object MakeReleaseCommand : CliktCommand(
    name = "make_release",
    help = "Make new GitHub release"
) {
    private val inputFile by option(help = "Path to release file")
        .path(mustExist = true).required()

    private val snapshot by option(help = "Is Snapshot release")
        .flag(default = false)

    private val gitTag by option(help = "Git Tag").required()
    private val commitHash by option(help = "Git Commit hash").default("")
    private val token by option(help = "Git Token").default("")

    override fun run() {
        exitProcess(tryReleaseFlank(inputFile, gitTag, commitHash, snapshot, token))
    }
}
