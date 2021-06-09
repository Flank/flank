package ftl.cli

import ftl.presentation.cli.MainCommand
import org.junit.Test
import picocli.CommandLine

class CliTest {

    @Test
    fun `all commands should contain help flags`() {
        val cmd = CommandLine(MainCommand())
        val allCommands = (cmd.subcommands.values.getAllSubCommands() + cmd)
        val commandsWithoutHelpFlag =
            allCommands
                .getCommandWithoutHelp()

        assert(commandsWithoutHelpFlag.isEmpty()) { "Help flag not found in command: flank ${commandsWithoutHelpFlag.joinToString { it.getCommandPath() }}" }
    }

    private fun CommandLine.getCommandPath(message: String = ""): String {
        return if (parent != null) parent.getCommandPath("$commandName $message")
        else message.trim()
    }

    private fun Collection<CommandLine>.getAllSubCommands(): Collection<CommandLine> =
        if (any()) (this + flatMap { it.subcommands.values }.getAllSubCommands())
        else this

    private fun Collection<CommandLine>.getCommandWithoutHelp() = map { command ->
        command to command.commandSpec.args().mapNotNull { arg -> arg as? CommandLine.Model.OptionSpec }
            .any { arg -> arg.names().contains("-h") || arg.names().contains("--help") }
    }.filter { (_, containsHelp) -> !containsHelp }.map { (command, _) -> command }
}
