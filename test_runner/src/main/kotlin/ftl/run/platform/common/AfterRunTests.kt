package ftl.run.platform.common

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.json.createSavedMatrix
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

private fun List<TestMatrix>.toSavedMatrixMap() =
    associate { matrix -> matrix.testMatrixId to createSavedMatrix(matrix) }

private fun saveConfigFile(matrixMap: MatrixMap, args: IArgs) {
    val configFilePath = if (args.useLocalResultDir())
        Paths.get(args.localResultDir, FtlConstants.configFileName(args)) else
        Paths.get(args.localResultDir, matrixMap.runPath, FtlConstants.configFileName(args))

    configFilePath.parent.toFile().mkdirs()
    Files.write(configFilePath, args.data.toByteArray())
}

internal suspend inline fun MatrixMap.printMatricesWebLinks(project: String) = coroutineScope {
    println("Matrices webLink")
    map.values.map {
        launch(Dispatchers.IO) {
            println("${FtlConstants.indent}${it.matrixId} ${getOrUpdateWebLink(it.webLink, project, it.matrixId)}")
        }
    }.joinAll()
    println()
}

private tailrec suspend fun getOrUpdateWebLink(link: String, project: String, matrixId: String): String =
    if (link.isNotBlank()) link
    else getOrUpdateWebLink(
        link = GcTestMatrix.refresh(matrixId, project).run { if (isInvalid()) "Unable to get web link" else webLink() },
        project = project,
        matrixId = matrixId
    )
