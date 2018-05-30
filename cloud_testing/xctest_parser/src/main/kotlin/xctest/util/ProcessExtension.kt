package xctest.util

import java.time.Duration
import java.util.concurrent.TimeUnit

data class ProcessResult(
        val stdout: String,
        val stderr: String
)

fun Process.failed(): Boolean {
    return this.exitValue() != 0
}

private val timeoutMs = Duration.ofMinutes(10).toMillis()

fun Process.waitForResult(): ProcessResult {
    val readInput = StreamReader(this.inputStream)
    readInput.start()
    val readError = StreamReader(this.errorStream)
    readError.start()

    this.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
    readInput.join(timeoutMs)
    readError.join(timeoutMs)

    return ProcessResult(stdout = readInput.text, stderr = readError.text)
}

