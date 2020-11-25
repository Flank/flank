package flank.scripts.shell.ops

import flank.scripts.shell.ops.BuildGameLoopExampleCommand
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option

object iOSOpsCommand : CliktCommand(name = "ios", help = "Build ios test artifacts") {

    private val generate: Boolean by option(help = "Make build").flag("-g", default = true)

    private val copy: Boolean by option(help = "Copy output files to tmp").flag("-c", default = true)

    override fun run() {
        if (generate.not()) return
        listOf(
            BuildEarlGreyExampleCommand,
            BuildGameLoopExampleCommand,
            BuildTestPlansExample,
            BuildFlankExampleCommand
        ).forEach {
            it.run()
        }
    }
}
