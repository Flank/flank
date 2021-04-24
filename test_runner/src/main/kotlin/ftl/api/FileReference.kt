package ftl.api

import ftl.adapter.GcStorageDownload

val downloadFileReference: FileReference.Download get() = GcStorageDownload
val uploadFileReference: FileReference.Download get() = TODO()
val existFileReference: FileReference.Exist get() = TODO()

data class FileReference(
    val local: String = "",
    val remote: String = ""
) {

    interface Download : (FileReference, Boolean, Boolean) -> FileReference
    interface Upload : (FileReference, Boolean) -> FileReference
    interface Exist : (FileReference) -> Boolean
}
