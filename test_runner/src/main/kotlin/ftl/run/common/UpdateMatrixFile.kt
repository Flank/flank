package ftl.run.common

import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal fun IArgs.updateMatrixFile(matrixMap: MatrixMap): Path {
    val matrixIdsPath = getMatrixFilePath(matrixMap)
    matrixIdsPath.parent.toFile().mkdirs()
    Files.write(matrixIdsPath, prettyPrint.toJson(matrixMap.map).toByteArray())
    return matrixIdsPath
}

internal fun IArgs.getMatrixFilePath(matrixMap: MatrixMap) =
    if (useLocalResultDir()) Paths.get(localResultDir, FtlConstants.matrixIdsFile)
    else Paths.get(localResultDir, matrixMap.runPath, FtlConstants.matrixIdsFile)
