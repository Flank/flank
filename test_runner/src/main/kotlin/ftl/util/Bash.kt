package ftl.util

import ftl.config.FtlConstants
import ftl.run.exception.FlankGeneralError
import java.lang.ProcessBuilder.Redirect.PIPE

object Bash {

    fun execute(cmd: String): String {
        println(cmd)

        val bashPath = if (FtlConstants.isWindows) "bash.exe" else "/bin/bash"

        val process = ProcessBuilder(bashPath, "-c", cmd)
            .redirectOutput(PIPE)
            .redirectError(PIPE)
            .start()

        val result = process.waitForResult()

        if (process.failed()) {
            System.err.println("Error: ${result.stderr}")
            throw FlankGeneralError("Command failed: $cmd")
        }

        return result.stdout.trim() + result.stderr.trim()
    }
}
