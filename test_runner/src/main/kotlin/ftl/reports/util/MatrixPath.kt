package ftl.reports.util

import java.io.File
import java.nio.file.Paths

fun File.getMatrixPath(objectName: String) = getShardName(objectName)?.asObjectPath()

private fun File.getShardName(
    objectName: String
) = shardNameRegex(objectName)
    .find(toString())
    ?.value
    ?.removePrefix("/")
    ?.removeSuffix("/")

private fun shardNameRegex(objectName: String) = "/($objectName)/(shard_|matrix_)\\d+(-rerun_\\d+)?/".toRegex()

private fun String.asObjectPath() = Paths.get(this).toString()
