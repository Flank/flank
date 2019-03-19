package ftl.util

import ftl.args.IArgs
import ftl.json.MatrixMap
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

fun resolveLocalRunPath(matrices: MatrixMap, args: IArgs): String {
    if (args.useLocalResultDir()) return args.localResultDir

    var runPath = File(matrices.runPath)
    if (!runPath.exists()) runPath = Paths.get(args.localResultDir, runPath.name).toFile()

    return runPath.toString()
}

fun testTimeoutToSeconds(timeout: String): Long {
    return when {
        timeout.contains("h") -> TimeUnit.HOURS.toSeconds(timeout.removeSuffix("h").toLong()) // Hours
        timeout.contains("m") -> TimeUnit.MINUTES.toSeconds(timeout.removeSuffix("m").toLong()) // Minutes
        timeout.contains("s") -> timeout.removeSuffix("s").toLong() // Seconds
        else -> timeout.removeSuffix("s").toLong() // Seconds
    }
}

fun validateTestShardIndex(testShardsIndex: Int, args: IArgs) {
    val testShardsTotal = args.testShardChunks.size
    if (testShardsIndex >= testShardsTotal) {
        throw IllegalArgumentException("Invalid test shard index $testShardsIndex not < $testShardsTotal")
    }

    if (testShardsIndex < 0) {
        throw IllegalArgumentException("Invalid test shard index $testShardsIndex not >= 0")
    }
}
