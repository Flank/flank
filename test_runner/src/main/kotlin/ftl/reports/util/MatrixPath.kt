package ftl.reports.util

import java.io.File
import java.nio.file.Paths

fun File.getMatrixPath(objectName: String) = getShardName()?.asObjectPath(objectName)

private fun File.getShardName() = shardNameRegex.find(toString())?.value?.removePrefix("/")?.removeSuffix("/")
private val shardNameRegex = "/.*(shard_|matrix_)\\d+(-rerun_\\d+)?/".toRegex()

private fun String.asObjectPath(objectName: String) = Paths.get(objectName, this).toString()
