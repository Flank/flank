package xctest

import java.io.InputStream
import java.lang.ProcessBuilder.Redirect.PIPE
import java.time.Duration
import java.util.concurrent.TimeUnit

object Bash {

    private val timeoutMs = Duration.ofMinutes(10).toMillis()

    private fun Process.failed(): Boolean {
        return this.exitValue() != 0
    }

    fun execute(cmd: String): String {
        println(cmd)

        val process = ProcessBuilder("/bin/bash", "-c", cmd)
                .redirectOutput(PIPE)
                .redirectError(PIPE)
                .start()

        val gobbleInput = StreamGobbler(process.inputStream)
        gobbleInput.start()
        val gobbleError = StreamGobbler(process.errorStream)
        gobbleError.start()

        process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
        gobbleInput.join(timeoutMs)
        gobbleError.join(timeoutMs)

        if (process.failed())  {
            System.err.println("Error: ${gobbleError.output}")
            throw RuntimeException("Command failed: $cmd")
        }

        return gobbleInput.output.trim()
    }
}
