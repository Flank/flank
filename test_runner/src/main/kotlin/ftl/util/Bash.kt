package ftl.util

import flank.common.isWindows
import flank.common.logLn
import ftl.run.exception.FlankGeneralError
import java.lang.ProcessBuilder.Redirect.PIPE
object Bash {

    fun execute(cmd: String, additionalPath: String = ""): String {
        logLn(cmd)

        val bashPath = if (isWindows) "bash.exe" else "/bin/bash"

        val process = ProcessBuilder(bashPath, "-c", cmd).also {
            if (additionalPath.isNotEmpty()) {
                val envs: MutableMap<String, String> = it.environment()
                envs["Path"] = additionalPath
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
