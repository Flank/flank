package flank.scripts.cli.shell.buildexample

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.shell.buildexample.ios.buildIosGameLoopExampleCommand

object BuildGameLoopExampleCommand : CliktCommand(
    name = "build_ios_gameloop_example",
    help = "Build ios game loop example app"
) {
    private val generate: Boolean? by option(help = "Make build")
        .flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp")
        .flag("-c", default = true)

    override fun run() {
        buildIosGameLoopExampleCommand(generate, copy)
    }
}
