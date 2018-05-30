package xctest.util

import java.util.concurrent.TimeUnit

data class ProcessResult(
        val stdout: String,
        val stderr: String
)

fun Process.failed(): Boolean {
    return this.exitValue() != 0
}

fun Process.waitForResult(): ProcessResult {
    val readInput = StreamReader(this.inputStream)
    readInput.start()
    val readError = StreamReader(this.errorStream)
    readError.start()

    this.waitFor(10, TimeUnit.MINUTES)
    readInput.join()
    readError.join()

    return ProcessResult(stdout = readInput.text, stderr = readError.text)
}
