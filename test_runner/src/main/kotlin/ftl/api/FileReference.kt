package ftl.api

import ftl.adapter.DownloadAsJunitXML
import ftl.adapter.GcStorageDownload

val downloadFileReference: FileReference.Download get() = GcStorageDownload
val downloadAsJunitXML: FileReference.DownloadAsXML get() = DownloadAsJunitXML

data class FileReference(
    val local: String = "",
    val remote: String = ""
) {

    interface Download : (FileReference, Boolean, Boolean) -> FileReference
    interface DownloadAsXML : (FileReference) -> JUnitTest.Result
}
