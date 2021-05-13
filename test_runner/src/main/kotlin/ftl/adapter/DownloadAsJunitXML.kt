package ftl.adapter

import ftl.api.FileReference
import ftl.api.JUnitTest
import ftl.api.emptyJunitTestResult
import ftl.client.google.downloadAsJunitXml

object DownloadAsJunitXML :
    FileReference.DownloadAsXML,
    (FileReference) -> JUnitTest.Result by { fileReference ->
        downloadAsJunitXml(fileReference) ?: emptyJunitTestResult()
    }
