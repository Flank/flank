package ftl.client.google

import ftl.api.FileReference
import ftl.client.junit.JUnitTestResult
import ftl.client.junit.parseAllSuitesXml
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

internal fun fileReferenceDownload(fileReference: FileReference, ifNeeded: Boolean, ignoreErrors: Boolean): String {
    if (!ignoreErrors) {
        if (fileReference.local.isBlank() && fileReference.remote.isBlank()) throw FlankGeneralError("Cannot create empty FileReference")
    }
    return when {
        ifNeeded -> {
            if (fileReference.local.isNotBlank()) fileReference.local
            else gcStorageDownload(fileReference.remote, ignoreErrors)
        }
        else -> gcStorageDownload(fileReference.remote, ignoreErrors)
    }
}

internal fun downloadAsJunitXml(fileReference: FileReference): JUnitTestResult? =
    fileReferenceDownload(fileReference, ifNeeded = false, ignoreErrors = true)
        .takeIf { it.isNotEmpty() }
        ?.let { parseAllSuitesXml(Paths.get(it)) }
