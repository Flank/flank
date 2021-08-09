package ftl.adapter

import ftl.api.Command
import ftl.client.runShellCommand

object CommandRunner :
    Command.Fetch,
    (Command) -> String by { (cmd, additionalPaths) ->
        runShellCommand(cmd, additionalPaths)
    }
