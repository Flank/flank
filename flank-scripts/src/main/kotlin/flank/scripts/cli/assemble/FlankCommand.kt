package flank.scripts.cli.assemble

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.assemble.buildFlank

object FlankCommand : CliktCommand(
    name = "flank",
    help = "Build Flank"
) {
    private val dirty: Boolean by option(
        "--dirty", "-d",
        help = "Do not clean before build"
    ).flag(
        default = false
    )

    override fun run() {
        buildFlank(clean = !dirty)
    }
}
