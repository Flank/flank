package flank.scripts.dependencies

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.dependencies.update.DependenciesUpdateCommand

object DependenciesCommand : CliktCommand(name = "dependencies", help = "Task for manages dependencies") {

    init {
        subcommands(DependenciesUpdateCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
