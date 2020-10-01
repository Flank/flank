package utils

import java.io.File
import java.util.concurrent.TimeUnit


fun String.runCommand(workingDir: File): ProcessResult = ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
    .directory(workingDir).startProcess()

private fun ProcessBuilder.startProcess() = start().run {
    waitFor(processTimeout, TimeUnit.MINUTES)
    ProcessResult(exitValue(), getOsSpecificOutput())
}

private fun Process.getOsSpecificOutput(): String {
    return if (isWindows) {
        File(outputFileName).readText() + File(errorFileName).readText()
    } else {
        inputStream.bufferedReader().readText() + this.errorStream.bufferedReader().readText()
    }
}

private fun ProcessBuilder.redirectOutputForCompatibleOS(): ProcessBuilder {
    return if (isWindows) {
        redirectOutput(File(outputFileName)).redirectError(File(errorFileName))
    } else {
        redirectOutput(ProcessBuilder.Redirect.PIPE).redirectError(ProcessBuilder.Redirect.PIPE)
    }
}

private val isWindows = System.getProperty("os.name").startsWith("Windows")

private const val outputFileName = "out.log"
private const val errorFileName = "error.log"
private const val processTimeout = 60L
