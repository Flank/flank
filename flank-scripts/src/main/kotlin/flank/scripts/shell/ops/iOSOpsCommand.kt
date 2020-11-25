package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

object IosOpsCommand : CliktCommand(name = "ios", help = "Build ios test artifacts") {

    private val generate: Boolean by option(help = "Make build").flag("-g", default = true)

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
