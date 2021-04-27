package ftl.run.common

import flank.common.OutputLogLevel
import flank.common.log
import flank.common.logLn
import flank.common.startWithNewLine
import ftl.api.Artifacts.DownloadPath
import ftl.api.Artifacts.Identity
import ftl.api.fetchArtifacts
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.util.Artifacts
import ftl.util.MatrixState
import kotlinx.coroutines.coroutineScope

internal suspend fun fetchAllTestRunArtifacts(matrixMap: MatrixMap, args: IArgs) = coroutineScope {
    logLn("FetchArtifacts".startWithNewLine(), OutputLogLevel.DETAILED)
    log(FtlConstants.indent)

    val downloadPath = DownloadPath(
        localResultDir = args.localResultDir,
        useLocalResultDir = args.useLocalResultDir(),
        keepFilePath = args.keepFilePath
    )

    val regexes = Artifacts.regexList(args)

    matrixMap.map.values
        .filter {
            val finished = it.state == MatrixState.FINISHED
            val notDownloaded = !it.downloaded
            finished && notDownloaded
        }
        .map {
            Identity(
                gcsPathWithoutRootBucket = it.gcsPathWithoutRootBucket,
                gcsRootBucket = it.gcsRootBucket,
                regex = regexes,
                downloadPath = downloadPath,
                matrixId = it.matrixId
            )
        }
        .map { fetchArtifacts(it) }
        .forEach { (matrixId, _) ->
            matrixMap.map[matrixId]?.downloaded = true
        }

    logLn(FtlConstants.indent + "Updating matrix file", level = OutputLogLevel.DETAILED)
    args.updateMatrixFile(matrixMap)
}
