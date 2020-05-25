package ftl.util

import ftl.config.FtlConstants
import ftl.gc.GcStorage

data class FileReference(
    val local: String = "",
    val gcs: String = ""
)

fun String.asFileReference(): FileReference =
    if (startsWith(FtlConstants.GCS_PREFIX))
        FileReference(gcs = this) else
        FileReference(local = this)

fun FileReference.downloadIfNeeded() = when {
    local.isNotBlank() -> this
    gcs.isNotBlank() -> copy(local = GcStorage.download(gcs))
    else -> this
}
