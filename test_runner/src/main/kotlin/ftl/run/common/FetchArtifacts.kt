package ftl.run.common

import com.google.cloud.storage.Storage
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.gc.GcStorage
import ftl.util.Artifacts
import ftl.util.MatrixState
import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

internal suspend fun fetchArtifacts(matrixMap: MatrixMap, args: IArgs) = coroutineScope {
    println("FetchArtifacts")
    val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)

    var dirty = false
    val filtered = matrixMap.map.values.filter {
        val finished = it.state == MatrixState.FINISHED
        val notDownloaded = !it.downloaded
        finished && notDownloaded
    }

    print(FtlConstants.indent)
    filtered.map { matrix ->
        launch(Dispatchers.IO) {
            val prefix = Storage.BlobListOption.prefix(matrix.gcsPathWithoutRootBucket)
            val result = GcStorage.storage.list(matrix.gcsRootBucket, prefix, fields)
            val artifactsList = Artifacts.regexList(args)

            result.iterateAll().forEach { blob ->
                val blobPath = blob.blobId.name
                val matched = artifactsList.any { blobPath.matches(it) }
                if (matched) {
                    val downloadFile = getDownloadPath(args, blobPath)

                    print(".")
                    if (!downloadFile.toFile().exists()) {
                        val parentFile = downloadFile.parent.toFile()
                        parentFile.mkdirs()
                        blob.downloadTo(downloadFile)
                    }
                }
            }

            dirty = true
            matrix.downloaded = true
        }
    }.joinAll()
    println()

    if (dirty) {
        println(FtlConstants.indent + "Updating matrix file")
        updateMatrixFile(matrixMap, args)
        println()
    }
}

internal fun getDownloadPath(args: IArgs, blobPath: String): Path {
    val localDir = args.localResultDir
    val parsed = Paths.get(blobPath)
    val objName = if (args.useLocalResultDir()) "" else parsed.getName(0).toString()
    // for iOS it is shardName, remove this comment after FTL introduce server side sharding for iOS
    val matrixName = parsed.getName(1).toString()
    val deviceName = parsed.getName(2).toString()
    val filePathName = if (args.keepFilePath) parsed.parent.drop(3).joinToString("/") else ""
    val fileName = parsed.fileName.toString()

    return Paths.get("$localDir/$objName/$matrixName/$deviceName/$filePathName/$fileName")
}
