package utils

import java.io.File
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File): ProcessResult = ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start().run {
        waitFor(60, TimeUnit.MINUTES)
        ProcessResult(exitValue(), inputStream.bufferedReader().readText())
    }



