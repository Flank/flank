package utils

import java.io.File
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File): ProcessResult = ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
    .directory(workingDir).run {
        if (System.getProperty("os.name").startsWith("Windows"))
            redirectOutput(File(outputFileName)).redirectError(File(errorFileName)).start().run {
                waitFor(processTimeout, TimeUnit.MINUTES)
                ProcessResult(exitValue(), File(outputFileName).readText() + File(errorFileName).readText())
            }
        else redirectOutput(ProcessBuilder.Redirect.PIPE).redirectError(ProcessBuilder.Redirect.PIPE).start().run {
            waitFor(processTimeout, TimeUnit.MINUTES)
            ProcessResult(
                exitValue(),
                inputStream.bufferedReader().readText() + this.errorStream.bufferedReader().readText()
            )
        }
    }

private const val outputFileName = "out.log"
private const val errorFileName = "error.log"
private const val processTimeout = 60L
