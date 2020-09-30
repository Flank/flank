package utils

import java.io.File
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File): ProcessResult = ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
        .directory(workingDir).run {
            if(System.getProperty("os.name").startsWith("Windows")) redirectOutput(File("out.txt"))
                        .redirectError(File("err.txt"))
                        .start().run {
                            waitFor(60, TimeUnit.MINUTES)
                            ProcessResult(exitValue(), File("out.txt").readText() + File("err.txt").readText())
                        }
             else redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start().run {
                            waitFor(60, TimeUnit.MINUTES)
                            ProcessResult(exitValue(), inputStream.bufferedReader().readText() + this.errorStream.bufferedReader().readText())
                        }

        }

