package xctest

import java.io.InputStream
import java.lang.ProcessBuilder.Redirect.PIPE
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private class StreamGobbler(private val inputStream: InputStream) : Runnable {
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

        val pool = Executors.newFixedThreadPool(2)
        val gobbleInput = StreamGobbler(process.inputStream)
        val gobbleError = StreamGobbler(process.errorStream)
        pool.submit(gobbleInput)
        pool.submit(gobbleError)

        process.waitFor(1, TimeUnit.MINUTES)

        if (process.failed()) throw RuntimeException("Command failed: $cmd")

        pool.shutdown()
        pool.awaitTermination(1, TimeUnit.MINUTES)

        return (gobbleInput.output + gobbleError.output).trim()
    }
}
