package ftl

import ftl.presentation.cli.MainCommand
import org.junit.Test
import picocli.CommandLine

class CliTest {
    @Test
    fun `all commands should contain help flags`() {
        val cmd = CommandLine(MainCommand())
        val allCommands = (cmd.subcommands.values.toList().getAllSubCommands() + cmd)
        allCommands.forEach { command ->
            val helpCommandExist = command.commandSpec.args().mapNotNull { arg -> arg as? CommandLine.Model.OptionSpec }
                .any { arg -> arg.names().contains("-h") && arg.names().contains("--help") }

            assert(helpCommandExist) { "Help flag not found in command: flank ${command.getCommandPath()}" }
        }
    }

    private fun CommandLine.getCommandPath(message: String = ""): String {
        return if (parent != null) parent.getCommandPath("$commandName $message")
        else message.trim()
    }

    fun List<CommandLine>.getAllSubCommands(): List<CommandLine> =
        if (any().not()) this
        else (this + flatMap { it.subcommands.values }.getAllSubCommands())
}
