package ftl.api

import ftl.adapter.GcStorageDownload
import ftl.run.exception.FlankGeneralError

val downloadFileReference: FileReference.Download get() = GcStorageDownload
val uploadFileReference: FileReference.Download get() = TODO()
val existFileReference: FileReference.Exist get() = TODO()

data class FileReference(
    val local: String = "",
    val remote: String = ""
) {
    init {
        if (local.isBlank() && remote.isBlank()) throw FlankGeneralError("Cannot create empty FileReference")
    }

    interface Download : (FileReference, Boolean, Boolean) -> FileReference
    interface Upload : (FileReference, Boolean) -> FileReference
    interface Exist : (FileReference) -> Boolean
}
