package ftl.client

import flank.common.isWindows
import ftl.run.exception.FlankGeneralError
import ftl.util.failed
import ftl.util.waitForResult

sealed class ShellEnvironment(val execution: String) {
    object Bash : ShellEnvironment("Bash.exe")
    object BinBash : ShellEnvironment("/bin/bash")
    object Cmd : ShellEnvironment("cmd.exe")
    object Default : ShellEnvironment(if (isWindows) "cmd.exe" else "/bin/bash")
}

fun runShellCommand(
    cmd: String,
    additionalPath: List<Pair<String, String>>
): String {
    val process = ProcessBuilder(shell.execution, command, cmd)
        .appendAdditionalPaths(additionalPath)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    val result = process.waitForResult()

    if (process.failed()) {
        System.err.println("Error: ${result.stderr}")
        throw FlankGeneralError("Command failed: $cmd")
    }

    return result.stdout.trim() + result.stderr.trim()
}

private fun ProcessBuilder.appendAdditionalPaths(
    additionalPath: List<Pair<String, String>>
) = apply {
    val envs: MutableMap<String, String> = environment()
    additionalPath.onEach { (key, value) -> envs[key] = value }
}

private val shell: ShellEnvironment by lazy {
    when {
        isWindows -> ShellEnvironment.Cmd
        else -> ShellEnvironment.BinBash
    }
}

private val command: String
    get() = if (isWindows) "/c" else "-c"
