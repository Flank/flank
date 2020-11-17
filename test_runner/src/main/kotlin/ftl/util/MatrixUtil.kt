package ftl.util

import ftl.args.IArgs
import ftl.json.MatrixMap
import java.io.File
import java.util.concurrent.TimeUnit

fun resolveLocalRunPath(matrices: MatrixMap, args: IArgs) = when {
    args.useLocalResultDir() -> args.localResultDir
    File(matrices.runPath).exists() -> matrices.runPath
    else -> join(args.localResultDir, matrices.runPath).also { localPath -> File(localPath).mkdirs() }
}

fun timeoutToSeconds(timeout: String): Long {
    return when {
        timeout.contains("h") -> TimeUnit.HOURS.toSeconds(timeout.removeSuffix("h").toLong()) // Hours
        timeout.contains("m") -> TimeUnit.MINUTES.toSeconds(timeout.removeSuffix("m").toLong()) // Minutes
        timeout.contains("s") -> timeout.removeSuffix("s").toLong() // Seconds
        else -> timeout.removeSuffix("s").toLong() // Seconds
    }
}

fun timeoutToMils(timeout: String): Long = timeoutToSeconds(timeout) * 1_000L
