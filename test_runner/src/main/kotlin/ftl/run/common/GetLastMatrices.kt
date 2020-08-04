package ftl.run.common

import com.google.gson.reflect.TypeToken
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.util.FlankCommonException
import java.nio.file.Paths

/** Reads in the last matrices from the localResultDir folder **/
internal fun getLastMatrices(args: IArgs): MatrixMap {
    val lastRun = args.getLastGcsPath() ?: throw FlankCommonException("no runs found in results/ folder")

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

    val listOfSavedMatrix = object : TypeToken<MutableMap<String, SavedMatrix>>() {}.type
    val map: MutableMap<String, SavedMatrix> = prettyPrint.fromJson(json, listOfSavedMatrix)

    return MatrixMap(map, path)
}
