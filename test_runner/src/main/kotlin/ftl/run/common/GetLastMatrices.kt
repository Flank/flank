package ftl.run.common

import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

/** Reads in the last matrices from the localResultDir folder **/
internal fun getLastMatrices(args: IArgs): MatrixMap {
    val lastRun = args.getLastGcsPath() ?: throw FlankGeneralError("no runs found in results/ folder")

    println("Loading run $lastRun")
    return matrixPathToObj(lastRun, args)
}

/** Creates MatrixMap from matrix_ids.json file */
internal fun matrixPathToObj(path: String, args: IArgs): MatrixMap {
    var filePath = Paths.get(path, FtlConstants.matrixIdsFile).toFile()
    if (!filePath.exists()) {
        filePath = Paths.get(args.localResultDir, path, FtlConstants.matrixIdsFile).toFile()
    }
    val json = filePath.readText()

    val map: MutableMap<String, SavedMatrix> = fromJson(json)

    return MatrixMap(map, path)
}
