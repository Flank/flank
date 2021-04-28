package ftl.api

import ftl.adapter.DownloadAsJunitXML
import ftl.adapter.GcStorageDownload
import ftl.reports.xml.model.JUnitTestResult

val downloadFileReference: FileReference.Download get() = GcStorageDownload
val downloadAsJunitXML: FileReference.DownloadAsXML get() = DownloadAsJunitXML

data class FileReference(
    val local: String = "",
    val remote: String = ""
) {

    interface Download : (FileReference, Boolean, Boolean) -> FileReference
    interface DownloadAsXML : (FileReference) -> JUnitTestResult
}
