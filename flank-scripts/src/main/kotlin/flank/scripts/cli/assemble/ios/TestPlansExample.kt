package flank.scripts.cli.assemble.ios

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import flank.scripts.ops.assemble.ios.buildTestPlansExample

object TestPlansExample : CliktCommand(
    name = "test_plans",
    help = "Build ios test plans example app"
) {
    private val generate: Boolean? by option(help = "Make build")
        .flag("-g", default = true)

    private val copy: Boolean? by option(help = "Copy output files to tmp")
        .flag("-c", default = true)

    override fun run() {
        buildTestPlansExample(generate, copy)
    }
}
