package xctest

import java.lang.ProcessBuilder.Redirect.PIPE
import java.util.concurrent.TimeUnit

object Bash {

    private fun Process.failed(): Boolean {
        return this.exitValue() != 0
    }

    private fun Process.output(): String {
        // must close the stream via use
        return this.inputStream.bufferedReader().use { it.readText().trim() }
    }

    fun execute(cmd: String): String {
        println(cmd)

        val process = ProcessBuilder("/bin/bash", "-c", cmd)
                .redirectOutput(PIPE)
                .redirectError(PIPE)
                .start()

        process.waitFor(2, TimeUnit.MINUTES)

        if (process.failed()) throw RuntimeException("Command failed: $cmd")

        return process.output()
    }
}
