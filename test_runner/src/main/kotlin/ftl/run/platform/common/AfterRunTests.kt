package ftl.run.platform.common

import flank.common.logLn
import flank.common.startWithNewLine
import ftl.api.RemoteStorage
import ftl.api.TestMatrix
import ftl.api.refreshTestMatrix
import ftl.api.uploadToRemoteStorage
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.GCS_STORAGE_LINK
import ftl.json.MatrixMap
import ftl.reports.addStepTime
import ftl.run.common.SESSION_ID_FILE
import ftl.run.common.saveSessionId
import ftl.run.common.updateMatrixFile
import ftl.util.StopWatch
import ftl.util.formatted
import ftl.util.isInvalid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths

internal suspend fun IArgs.afterRunTests(
    testMatrices: List<TestMatrix.Data>,
    stopwatch: StopWatch,
) = MatrixMap(
    map = testMatrices.toSavedMatrixMap(),
    runPath = resultsDir
).also { matrixMap ->
    updateMatrixFile(matrixMap)
    saveConfigFile(matrixMap)
    saveSessionId()
    uploadSessionId()
    logLn(FtlConstants.indent + "${matrixMap.map.size} matrix ids created in ${stopwatch.check().formatted()}")
    val gcsBucket = GCS_STORAGE_LINK + resultsBucket + "/" + matrixMap.runPath
    logLn("${FtlConstants.indent}Raw results will be stored in your GCS bucket at [$gcsBucket]")
    matrixMap.printMatricesWebLinks(project)
    addStepTime("Running tests", stopwatch.check())
}

private fun List<TestMatrix.Data>.toSavedMatrixMap() =
    associate { matrix -> matrix.matrixId to matrix }

private fun IArgs.saveConfigFile(matrixMap: MatrixMap) {
    val configFilePath = if (useLocalResultDir())
        Paths.get(localResultDir, FtlConstants.configFileName(this)) else
        Paths.get(localResultDir, matrixMap.runPath, FtlConstants.configFileName(this))

    configFilePath.parent.toFile().mkdirs()
    Files.write(configFilePath, data.toByteArray())
}

internal suspend inline fun MatrixMap.printMatricesWebLinks(project: String) = coroutineScope {
    logLn("Matrices webLink".startWithNewLine())
    map.values.map {
        launch(Dispatchers.IO) {
            logLn("${FtlConstants.indent}${it.matrixId} ${getOrUpdateWebLink(it.webLink, project, it.matrixId)}")
        }
    }.joinAll()
    logLn()
}

private tailrec suspend fun getOrUpdateWebLink(link: String, project: String, matrixId: String): String =
    if (link.isNotBlank()) link
    else getOrUpdateWebLink(
        link = refreshTestMatrix(
            TestMatrix.Identity(
                matrixId,
                project
            )
        ).run { if (isInvalid) "Unable to get web link" else webLink },
        project = project,
        matrixId = matrixId
    )

fun IArgs.uploadSessionId() = takeUnless { disableResultsUpload }?.let {
    val file = Paths.get(localResultDir, SESSION_ID_FILE).toString()
    if (file.startsWith(FtlConstants.GCS_PREFIX)) return file
    uploadToRemoteStorage(
        RemoteStorage.Dir(resultsBucket, resultsDir),
        RemoteStorage.Data(file, Files.readAllBytes(Paths.get(file)))
    )
}
