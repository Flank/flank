package ftl.run.common

import com.google.cloud.storage.Storage
import flank.common.OutputLogLevel
import flank.common.log
import flank.common.logLn
import flank.common.startWithNewLine
import ftl.args.IArgs
import ftl.client.google.GcStorage
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.util.Artifacts
import ftl.util.MatrixState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.nio.file.Paths

// TODO needs refactor
internal suspend fun fetchArtifacts(matrixMap: MatrixMap, args: IArgs) = coroutineScope {
    logLn("FetchArtifacts".startWithNewLine(), OutputLogLevel.DETAILED)
    val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)

    var dirty = false
    val filtered = matrixMap.map.values.filter {
        val finished = it.state == MatrixState.FINISHED
        val notDownloaded = !it.downloaded
        finished && notDownloaded
    }.toMutableList()

    log(FtlConstants.indent)
    filtered.flatMapIndexed { index, matrix ->
        val prefix = Storage.BlobListOption.prefix(matrix.gcsPathWithoutRootBucket)
        val result = GcStorage.storage.list(matrix.gcsRootBucket, prefix, fields)
        val artifactsList = Artifacts.regexList(args)
        val jobs = mutableListOf<Job>()

        result.iterateAll().forEach { blob ->
            val blobPath = blob.blobId.name
            val matched = artifactsList.any { blobPath.matches(it) }
            if (matched) {
                val downloadFile = getDownloadPath(args, blobPath)
                if (!downloadFile.toFile().exists()) {
                    val parentFile = downloadFile.parent.toFile()
                    parentFile.mkdirs()
                    jobs += launch(Dispatchers.IO) { blob.downloadTo(downloadFile) }
                }
            }
        }

        dirty = true
        filtered[index] = matrix.copy(downloaded = true)
        jobs
    }.joinAll()

    if (dirty) {
        logLn(FtlConstants.indent + "Updating matrix file", level = OutputLogLevel.DETAILED)
        args.updateMatrixFile(matrixMap)
    }
}

internal fun getDownloadPath(args: IArgs, blobPath: String): Path {
    val localDir = args.localResultDir
    val parsed = Paths.get(blobPath)
    val objName = if (args.useLocalResultDir()) "" else parsed.getName(0).toString()
    // for iOS it is shardName, remove this comment after FTL introduce server side sharding for iOS
    val matrixName = parsed.getName(1).toString()
    val fileName = parsed.fileName.toString()
    val deviceName = parsed.getName(2).toString().takeUnless { it == fileName }.orEmpty()
    val filePathName = if (args.keepFilePath) parsed.parent.drop(3).joinToString("/") else ""

    return Paths.get("$localDir/$objName/$matrixName/$deviceName/$filePathName/$fileName")
}
