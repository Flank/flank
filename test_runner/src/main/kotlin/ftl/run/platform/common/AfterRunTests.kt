package ftl.run.platform.common

import com.google.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.json.createSavedMatrix
import ftl.log.logLine
import ftl.run.common.updateMatrixFile
import ftl.util.StopWatch
import ftl.util.isInvalid
import ftl.util.webLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths

internal suspend fun IArgs.afterRunTests(
    testMatrices: List<TestMatrix>,
    stopwatch: StopWatch,
) = MatrixMap(
    map = testMatrices.toSavedMatrixMap(),
    runPath = resultsDir
).also { matrixMap ->
    updateMatrixFile(matrixMap)
    saveConfigFile(matrixMap)

    logLine(FtlConstants.indent + "${matrixMap.map.size} matrix ids created in ${stopwatch.check()}")
    val gcsBucket = "https://console.developers.google.com/storage/browser/" +
            resultsBucket + "/" + matrixMap.runPath
    logLine(FtlConstants.indent + gcsBucket)
    logLine()
    matrixMap.printMatricesWebLinks(project)
}

private fun List<TestMatrix>.toSavedMatrixMap() =
    associate { matrix -> matrix.testMatrixId to createSavedMatrix(matrix) }

private fun IArgs.saveConfigFile(matrixMap: MatrixMap) {
    val configFilePath = if (useLocalResultDir())
        Paths.get(localResultDir, FtlConstants.configFileName(this)) else
        Paths.get(localResultDir, matrixMap.runPath, FtlConstants.configFileName(this))

    configFilePath.parent.toFile().mkdirs()
    Files.write(configFilePath, data.toByteArray())
}

internal suspend inline fun MatrixMap.printMatricesWebLinks(project: String) = coroutineScope {
    logLine("Matrices webLink")
    map.values.map {
        launch(Dispatchers.IO) {
            logLine("${FtlConstants.indent}${it.matrixId} ${getOrUpdateWebLink(it.webLink, project, it.matrixId)}")
        }
    }.joinAll()
    logLine()
}

private tailrec suspend fun getOrUpdateWebLink(link: String, project: String, matrixId: String): String =
    if (link.isNotBlank()) link
    else getOrUpdateWebLink(
        link = GcTestMatrix.refresh(matrixId, project).run { if (isInvalid()) "Unable to get web link" else webLink() },
        project = project,
        matrixId = matrixId
    )
