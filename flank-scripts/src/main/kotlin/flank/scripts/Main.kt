package flank.scripts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.ci.CiCommand
import flank.scripts.release.ReleaseCommand

class Main : CliktCommand(name = "flankScripts") {
    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}

fun main(args: Array<String>) {
    Main()
        .subcommands(ReleaseCommand(), CiCommand())
        .main(args)
}
