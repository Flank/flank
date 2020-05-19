package ftl.run.common

import com.google.cloud.storage.Storage
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import ftl.util.Artifacts
import ftl.util.MatrixState
import ftl.util.ObjPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.nio.file.Paths

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
    val p = if (args is AndroidArgs)
        ObjPath.parse(blobPath) else
        ObjPath.legacyParse(blobPath)

    // Store downloaded artifacts at device root.
    return Paths.get(
        localDir,
        if (args.useLocalResultDir().not()) p.objName else "",
        p.matrixName,
        p.shardName,
        p.deviceName,
        if (args.keepFilePath) p.filePathName else "",
        p.fileName
    )
}
