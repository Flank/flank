package ftl.util

import flank.common.isWindows
import flank.common.logLn
import ftl.run.exception.FlankGeneralError
import java.lang.ProcessBuilder.Redirect.PIPE

object Bash {
    fun execute(
        cmd: String,
        additionalPath: List<Pair<String, String>> = emptyList(),
        shellEnvironment: ShellEnvironment = ShellEnvironment.Default
    ): String {
        logLn(cmd)
        val process = ProcessBuilder(shellEnvironment.execution, "-c", cmd).also {
            if (additionalPath.isNotEmpty()) {
                val envs: MutableMap<String, String> = it.environment()
                additionalPath.forEach { extra ->
                    envs[extra.component1()] = extra.component2()
                }
            }
        }.redirectOutput(PIPE).redirectError(PIPE).start()

        val result = process.waitForResult()

        if (process.failed()) {
            System.err.println("Error: ${result.stderr}")
            throw FlankGeneralError("Command failed: $cmd")
        }

        return result.stdout.trim() + result.stderr.trim()
    }
}

sealed class ShellEnvironment(val execution: String) {
    object Bash : ShellEnvironment("Bash.exe")
    object BinBash : ShellEnvironment("/bin/bash")
    object Cmd : ShellEnvironment("cmd.exe")
    object Default : ShellEnvironment(if (isWindows) Bash.execution else BinBash.execution)
}
