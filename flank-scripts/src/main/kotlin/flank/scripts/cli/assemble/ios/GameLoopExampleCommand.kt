package flank.scripts.cli.assemble.ios

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.assemble.ios.buildIosGameLoopExampleCommand

object GameLoopExampleCommand : CliktCommand(
    name = "game_loop",
    help = "Assemble iOS game loop application"
) {
    private val generate: Boolean? by option(help = "Make build")
        .flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp")
        .flag("-c", default = true)

    override fun run() {
        buildIosGameLoopExampleCommand(generate, copy)
    }
}
