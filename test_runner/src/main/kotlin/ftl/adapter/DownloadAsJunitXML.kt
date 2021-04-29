package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.FileReference
import ftl.api.JUnitTest
import ftl.client.google.downloadAsJunitXml

object DownloadAsJunitXML :
    FileReference.DownloadAsXML,
    (FileReference) -> JUnitTest.Result by { fileReference ->
        downloadAsJunitXml(fileReference).toApiModel()
    }
