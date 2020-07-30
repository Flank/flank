package flank.scripts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.release.ReleaseCommand

class Main : CliktCommand(name = "flankScripts") {
    override fun run() {}
}

fun main(args: Array<String>) {
    Main().subcommands(ReleaseCommand())
            .main(args)
}
