package ftl.run.platform.common

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.json.SavedMatrix
import ftl.run.common.updateMatrixFile
import ftl.util.StopWatch
import ftl.util.webLink
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths

internal suspend fun afterRunTests(
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

    matrixMap.printMatricesWebLinks(config.project)
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

private suspend inline fun MatrixMap.printMatricesWebLinks(project: String) = coroutineScope {
    println("Matrices webLink")
    map.values.map { launch { it.printWebLink(project) } }.joinAll()
    println()
}

private suspend inline fun SavedMatrix.printWebLink(project: String) =
    println("${FtlConstants.indent}$matrixId: ${getOrUpdateWebLink(webLink, project, matrixId)}")

private tailrec suspend fun getOrUpdateWebLink(link: String, project: String, matrixId: String): String =
    if (link.isNotBlank()) link
    else getOrUpdateWebLink(GcTestMatrix.refresh(matrixId, project).webLink(), project, matrixId)
