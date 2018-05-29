package xctest

import java.io.InputStream
import java.lang.ProcessBuilder.Redirect.PIPE

private class StreamGobbler(private val inputStream: InputStream) : Thread() {
    var output: String = ""

    override fun run() {
        // .use is required to close the input stream
        output = inputStream.bufferedReader().use { it.readText() }
    }
}

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

        val gobbleInput = StreamGobbler(process.inputStream)
        gobbleInput.start()
        val gobbleError = StreamGobbler(process.errorStream)
        gobbleError.start()

        process.waitFor()
        gobbleInput.join()
        gobbleError.join()

        if (process.failed()) throw RuntimeException("Command failed: $cmd")

        return (gobbleInput.output + gobbleError.output).trim()
    }
}
