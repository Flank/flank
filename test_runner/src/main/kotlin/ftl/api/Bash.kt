package ftl.api

import ftl.adapter.CommandRunner

val runCommand: Command.Fetch get() = CommandRunner

data class Command(
    val cmd: String,
    val additionalPath: List<Pair<String, String>> = emptyList()
) {
    interface Fetch : (Command) -> String
}
