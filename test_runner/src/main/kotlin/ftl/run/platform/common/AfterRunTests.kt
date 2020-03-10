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
    jobs: List<TestMatrix>,
    runGcsPath: String,
    stopwatch: StopWatch,
    config: IArgs
): MatrixMap {
    val savedMatrices = mutableMapOf<String, SavedMatrix>()

    jobs.forEach { matrix ->
        val matrixId = matrix.testMatrixId
        savedMatrices[matrixId] = SavedMatrix(matrix)
    }

    val matrixMap = MatrixMap(savedMatrices, runGcsPath)
    updateMatrixFile(matrixMap, config)
    saveConfigFile(matrixMap, config)

    println(FtlConstants.indent + "${savedMatrices.size} matrix ids created in ${stopwatch.check()}")
    val gcsBucket = "https://console.developers.google.com/storage/browser/" +
            config.resultsBucket + "/" + matrixMap.runPath
    println(FtlConstants.indent + gcsBucket)
    println()

    return matrixMap
}

private fun saveConfigFile(matrixMap: MatrixMap, args: IArgs) {
    val configFilePath = if (args.useLocalResultDir()) {
        Paths.get(args.localResultDir, FtlConstants.configFileName(args))
    } else {
        Paths.get(args.localResultDir, matrixMap.runPath, FtlConstants.configFileName(args))
    }

    configFilePath.parent.toFile().mkdirs()
    Files.write(configFilePath, args.data.toByteArray())
}
