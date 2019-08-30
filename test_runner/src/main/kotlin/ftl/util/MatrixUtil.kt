package ftl.util

import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.util.Utils.join
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

fun resolveLocalRunPath(matrices: MatrixMap, args: IArgs): String {
    if (args.useLocalResultDir()) return args.localResultDir

    val runPath = File(matrices.runPath)
    if (!runPath.exists()) return join(args.localResultDir, runPath.name)

    // avoid File().toString() as that will use a '\' path separator.
    return matrices.runPath
}

fun testTimeoutToSeconds(timeout: String): Long {
    return when {
        timeout.contains("h") -> TimeUnit.HOURS.toSeconds(timeout.removeSuffix("h").toLong()) // Hours
        timeout.contains("m") -> TimeUnit.MINUTES.toSeconds(timeout.removeSuffix("m").toLong()) // Minutes
        timeout.contains("s") -> timeout.removeSuffix("s").toLong() // Seconds
        else -> timeout.removeSuffix("s").toLong() // Seconds
    }
}
