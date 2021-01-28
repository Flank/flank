package flank.scripts.cli.shell.buildexample

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.shell.buildexample.ios.buildEarlGreyExample

object BuildEarlGreyExampleCommand : CliktCommand(
    name = "build_earl_grey_example",
    help = "Build ios earl grey example app with tests"
) {
    private val generate: Boolean? by option(help = "Make build")
        .flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp")
        .flag("-c", default = true)

    override fun run() {
        buildEarlGreyExample(generate, copy)
    }
}
