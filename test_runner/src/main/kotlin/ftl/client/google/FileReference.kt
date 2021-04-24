package ftl.client.google

import ftl.api.RemoteStorage
import ftl.api.uploadToRemoteStorage
import ftl.args.IArgs
import ftl.run.exception.FlankGeneralError
import java.nio.file.Files
import java.nio.file.Paths

data class FileReference(
    val local: String = "",
    val gcs: String = ""
) {
    init {
        assertNotEmpty()
    }
}

private fun FileReference.assertNotEmpty() {
    if (local.isBlank() && gcs.isBlank())
        throw FlankGeneralError("Cannot create empty FileReference")
}

fun IArgs.uploadIfNeeded(file: FileReference): FileReference =
    file.uploadIfNeeded(
        rootGcsBucket = resultsBucket,
        runGcsPath = resultsDir
    )

fun FileReference.uploadIfNeeded(
    rootGcsBucket: String,
    runGcsPath: String
): FileReference =
    if (gcs.isNotBlank()) this
    else copy(gcs = upload(local, rootGcsBucket, runGcsPath))

fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String {
    return uploadToRemoteStorage(
        RemoteStorage.Dir(rootGcsBucket, runGcsPath),
        RemoteStorage.Data(file, Files.readAllBytes(Paths.get(file)))
    )
}
