package flank.scripts.shell.ops

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class OpsCommand : CliktCommand(name = "ops", help = "Contains all ops command: android, ios, gp") {
   init {
       subcommands(
           AndroidOpsCommand(),
           IosOpsCommand(),
           GoOpsCommand()
       )
   }

    override fun run() {

    }

}
