package ftl.run.common

import ftl.args.IArgs
import java.io.File
import java.nio.file.Paths

internal fun IArgs.getLastGcsPath(): String? = Paths
    .get(localResultDir)
    .toFile()
    .listFiles()
    ?.getLastModifiedDirectory()
    ?.name

private fun Array<File>.getLastModifiedDirectory(): File? = this
    .filter(File::isDirectory)
    .maxByOrNull(File::lastModified)
