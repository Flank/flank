package ftl.run.platform.common

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.run.common.updateMatrixFile
import ftl.util.StopWatch
import java.nio.file.Files
import java.nio.file.Paths

internal fun afterRunTests(
    testMatrices: List<TestMatrix>,
    runGcsPath: String,
    stopwatch: StopWatch,
    config: IArgs
) = MatrixMap(
    map = testMatrices.toSavedMatrixMap(),
    runPath = runGcsPath
).also { matrixMap ->
    updateMatrixFile(matrixMap, config)
    saveConfigFile(matrixMap, config)

    println(FtlConstants.indent + "${matrixMap.map.size} matrix ids created in ${stopwatch.check()}")
    val gcsBucket = "https://console.developers.google.com/storage/browser/" +
            config.resultsBucket + "/" + matrixMap.runPath
    println(FtlConstants.indent + gcsBucket)
    println()
}

private fun List<TestMatrix>.toSavedMatrixMap() = this
    .map { matrix -> matrix.testMatrixId to SavedMatrix(matrix) }
    .toMap()

private fun saveConfigFile(matrixMap: MatrixMap, args: IArgs) {
    val configFilePath = if (args.useLocalResultDir())
        Paths.get(args.localResultDir, FtlConstants.configFileName(args)) else
        Paths.get(args.localResultDir, matrixMap.runPath, FtlConstants.configFileName(args))

    configFilePath.parent.toFile().mkdirs()
    Files.write(configFilePath, args.data.toByteArray())
}
