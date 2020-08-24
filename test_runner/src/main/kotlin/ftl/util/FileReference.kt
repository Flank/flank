package ftl.util

import ftl.config.FtlConstants
import ftl.gc.GcStorage
import ftl.run.exception.FlankGeneralError

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

fun String.asFileReference(): FileReference =
    if (startsWith(FtlConstants.GCS_PREFIX))
        FileReference(gcs = this) else
        FileReference(local = this)

fun FileReference.downloadIfNeeded() =
    if (local.isNotBlank()) this
    else copy(local = GcStorage.download(gcs))

fun FileReference.uploadIfNeeded(rootGcsBucket: String, runGcsPath: String) =
    if (gcs.isNotBlank()) this
    else copy(gcs = GcStorage.upload(local, rootGcsBucket, runGcsPath))
