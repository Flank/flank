package ftl.util

import ftl.api.FileReference
import ftl.api.RemoteStorage
import ftl.api.downloadFileReference
import ftl.api.uploadToRemoteStorage
import ftl.args.IArgs
import ftl.config.FtlConstants
import java.nio.file.Files
import java.nio.file.Paths

fun String.asFileReference(): FileReference =
    if (startsWith(FtlConstants.GCS_PREFIX)) FileReference(remote = this) else FileReference(local = this)

fun IArgs.getSmartFlankGCSPathAsFileReference() = this.smartFlankGcsPath.asFileReference()

internal fun FileReference.downloadIfNeeded() = downloadFileReference(this, true, false)

fun IArgs.uploadIfNeeded(file: FileReference): FileReference =
    file.uploadIfNeeded(
        rootGcsBucket = resultsBucket,
        runGcsPath = resultsDir
    )

fun FileReference.uploadIfNeeded(
    rootGcsBucket: String,
    runGcsPath: String
): FileReference = if (remote.isNotBlank()) this else copy(remote = upload(local, rootGcsBucket, runGcsPath))

fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String {
    return uploadToRemoteStorage(
        RemoteStorage.Dir(rootGcsBucket, runGcsPath),
        RemoteStorage.Data(file, Files.readAllBytes(Paths.get(file)))
    )
}
