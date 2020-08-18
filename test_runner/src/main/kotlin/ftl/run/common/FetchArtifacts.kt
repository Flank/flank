package ftl.run.common

import com.google.cloud.storage.Blob
import com.google.cloud.storage.Storage
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.json.MatrixMap
import ftl.gc.GcStorage
import ftl.json.SavedMatrix
import ftl.util.Artifacts
import ftl.util.MatrixState
import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

internal suspend fun fetchArtifacts(matrixMap: MatrixMap, args: IArgs) = coroutineScope {
    val artifactsList = Artifacts.regexList(args)
    var dirty = false

    fun SavedMatrix.getBlobs() = Triple(
        GcStorage.storage.list(
            gcsRootBucket,
            Storage.BlobListOption.prefix(gcsPathWithoutRootBucket),
            Storage.BlobListOption.fields(Storage.BlobField.NAME)
        ).iterateAll(), artifactsList, args
    )

    println("FetchArtifacts")
    print(FtlConstants.indent)
    matrixMap
        .onlyFinishedMatrices()
        .onlyNotDownloadedMatrices()
        .apply { if (isNotEmpty()) dirty = true }
        .flatMap { savedMatrix ->
            savedMatrix
                .apply { downloaded = true }
                .getBlobs()
                .takeMatchedByArtifactList()
                .getDownloadPaths()
                .onlyNotExisting()
                .prepareDirectories()
                .downloadAsync()
        }.joinAll()
    println()

    if (dirty) {
        println(FtlConstants.indent + "Updating matrix file")
        updateMatrixFile(matrixMap, args)
        println()
    }
}

private fun MatrixMap.onlyFinishedMatrices() = map.values.filter { it.state == MatrixState.FINISHED }

private fun List<SavedMatrix>.onlyNotDownloadedMatrices() = filterNot { it.downloaded }

private fun Triple<MutableIterable<Blob>, List<Regex>, IArgs>.takeMatchedByArtifactList() = first
    .asSequence()
    .filter { blob -> second.any { blob.blobId.name.matches(it) } }
    .map { it to third }

private fun Sequence<Pair<Blob, IArgs>>.getDownloadPaths() = map { (blob, args) -> blob to getDownloadPath(args, blob.blobId.name) }

private fun Sequence<Pair<Blob, Path>>.onlyNotExisting() = filterNot { (_, path) -> path.toFile().exists() }

private fun Sequence<Pair<Blob, Path>>.prepareDirectories() = map { (blob, path) ->
    print(".")
    path.parent.toFile().mkdirs();
    { blob.downloadTo(path) }
}

private suspend fun Sequence<() -> Unit>.downloadAsync() = coroutineScope { map { launch(Dispatchers.IO) { it() } }.toList() }

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
