package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.FileReference
import ftl.client.google.downloadAsJunitXml
import ftl.reports.xml.model.JUnitTestResult

object DownloadAsJunitXML :
    FileReference.DownloadAsXML,
    (FileReference) -> JUnitTestResult by { fileReference ->
        downloadAsJunitXml(fileReference).toApiModel()
    }
