package flank.scripts.integration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object IntegrationCommand : CliktCommand(name = "integration") {

    init {
        subcommands(ProcessResultCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
