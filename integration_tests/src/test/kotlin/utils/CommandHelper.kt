package utils

import java.io.File
import java.util.concurrent.TimeUnit


fun String.runCommand(workingDir: File, testSuite: String): ProcessResult {
    val outFile = File("$testSuite-$outputFileName").also { it.deleteOnExit() }
    val errFile = File("$testSuite-$errorFileName").also { it.deleteOnExit() }
    val result = ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
        .directory(workingDir)
        .redirectOutput(outFile)
        .redirectError(errFile)
        .start().also { it.waitFor(processTimeout, TimeUnit.MINUTES) }
    File("$testSuite-compare").writeText(File(outFile.name).readText() + File(errFile.name).readText())
    return ProcessResult(result.exitValue(), File(outFile.name).readText() + File(errFile.name).readText())
}

private const val outputFileName = "out.log"
private const val errorFileName = "error.log"
private const val processTimeout = 60L
