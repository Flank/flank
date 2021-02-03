package flank.scripts.cli.shell.buildexample

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.shell.buildexample.ios.buildIosFlankExample

object BuildFlankExampleCommand : CliktCommand(
    name = "build_flank_example",
    help = "Build ios flank example app with tests"
) {
    private val generate: Boolean? by option(help = "Make build")
        .flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp")
        .flag("-c", default = true)

    override fun run() {
        buildIosFlankExample(generate, copy)
    }
}
