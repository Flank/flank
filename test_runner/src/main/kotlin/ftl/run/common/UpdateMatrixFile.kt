package ftl.run.common

import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal fun updateMatrixFile(matrixMap: MatrixMap, args: IArgs): Path {
    val matrixIdsPath = if (args.useLocalResultDir()) {
        Paths.get(args.localResultDir, FtlConstants.matrixIdsFile)
    } else {
        Paths.get(args.localResultDir, matrixMap.runPath, FtlConstants.matrixIdsFile)
    }
    matrixIdsPath.parent.toFile().mkdirs()
    Files.write(matrixIdsPath, prettyPrint.toJson(matrixMap.map).toByteArray())
    return matrixIdsPath
}
