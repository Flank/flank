package flank.scripts.cli.assemble.ios

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.assemble.ios.buildEarlGreyExample

object EarlGreyCommand : CliktCommand(
    name = "earl_grey",
    help = "Assemble iOS earl grey application"
) {
    private val generate: Boolean? by option(help = "Make build")
        .flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp")
        .flag("-c", default = true)

    override fun run() {
        buildEarlGreyExample(generate, copy)
    }
}
