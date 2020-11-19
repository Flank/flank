package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object OpsCommand : CliktCommand(name = "ops", help = "Contains all ops command: android, ios, gp") {
    init {
        subcommands(
            AndroidOpsCommand,
            BuildEarlGreyExampleCommand,
            BuildGameLoopExampleCommand,
            BuildMultiTestTargetsExample,
            BuildFlankExampleCommand,
            GoOpsCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
