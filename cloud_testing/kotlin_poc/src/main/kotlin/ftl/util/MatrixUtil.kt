package ftl.util

import ftl.config.FtlConstants
import ftl.json.MatrixMap
import java.io.File
import java.nio.file.Paths

fun resolveLocalRunPath(matrices: MatrixMap): String {
    var runPath = File(matrices.runPath)
    if (!runPath.exists()) runPath = Paths.get(FtlConstants.localResultsDir, runPath.name).toFile()

    return runPath.toString()
}
