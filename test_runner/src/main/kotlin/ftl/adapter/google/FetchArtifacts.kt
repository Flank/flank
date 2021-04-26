package ftl.adapter.google

import com.google.cloud.storage.Storage
import ftl.api.Artifacts.DownloadPath
import ftl.api.Artifacts.Identity
import ftl.api.MatrixId
import ftl.client.google.GcStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.nio.file.Path
import java.nio.file.Paths

suspend fun fetchArtifacts(identity: Identity): Pair<MatrixId, List<String>> = coroutineScope {
    val prefix = Storage.BlobListOption.prefix(identity.gcsPathWithoutRootBucket)
    val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)
    val result = GcStorage.storage.list(identity.gcsRootBucket, prefix, fields)

    MatrixId(identity.matrixId) to result.iterateAll().mapNotNull { blob ->
        val blobPath = blob.blobId.name
        val matched = identity.regex.any { blobPath.matches(it) }
        val downloadFile = getFilePathToDownload(identity.downloadPath, blobPath)

        if (matched && !downloadFile.toFile().exists()) {
            val parentFile = downloadFile.parent.toFile()
            parentFile.mkdirs()
            async(Dispatchers.IO) {
                blob.downloadTo(downloadFile)
                downloadFile.toAbsolutePath().toString()
            }
        } else null
    }.awaitAll()
}

internal fun getFilePathToDownload(downloadPath: DownloadPath, blobPath: String): Path {
    val localDir = downloadPath.localResultDir
    val parsed = Paths.get(blobPath)
    val objName = if (downloadPath.useLocalResultDir) "" else parsed.getName(0).toString()
    // for iOS it is shardName, remove this comment after FTL introduce server side sharding for iOS
    val matrixName = parsed.getName(1).toString()
    val fileName = parsed.fileName.toString()
    val deviceName = parsed.getName(2).toString().takeUnless { it == fileName }.orEmpty()
    val filePathName = if (downloadPath.keepFilePath) parsed.parent.drop(3).joinToString("/") else ""

    return Paths.get("$localDir/$objName/$matrixName/$deviceName/$filePathName/$fileName")
}

// private val fields = Storage.BlobListOption.fields(Storage.BlobField.NAME)
