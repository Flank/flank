package ftl.api

val downloadFileReference: FileReference.Download get() = TODO()
val uploadFileReference: FileReference.Download get() = TODO()
val existFileReference: FileReference.Exist get() = TODO()

data class FileReference(
    val local: String = "",
    val remote: String = ""
) {
    interface Download : (FileReference) -> FileReference
    interface Upload : (FileReference) -> FileReference
    interface Exist : (FileReference) -> Boolean
}
