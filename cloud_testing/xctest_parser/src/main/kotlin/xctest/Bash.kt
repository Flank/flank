package xctest

import java.lang.ProcessBuilder.Redirect.PIPE

object Bash {

    private fun Process.failed(): Boolean {
        return this.exitValue() != 0
    }

    fun execute(cmd: String): String {
        println(cmd)

        val process = ProcessBuilder("/bin/bash", "-c", cmd)
                .redirectOutput(PIPE)
                .redirectError(PIPE)
                .start()

        if (process.failed()) throw RuntimeException("Command failed: $cmd")

        return process.inputStream.bufferedReader().readText().trim()
    }
}
