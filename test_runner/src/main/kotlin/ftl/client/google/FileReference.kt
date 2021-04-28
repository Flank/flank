package ftl.client.google

import ftl.api.FileReference
import ftl.api.RemoteStorage
import ftl.api.uploadToRemoteStorage
import ftl.args.IArgs
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.parseAllSuitesXml
import ftl.run.exception.FlankGeneralError
import java.nio.file.Files
import java.nio.file.Paths

fun IArgs.uploadIfNeeded(file: FileReference): FileReference =
    file.uploadIfNeeded(
        rootGcsBucket = resultsBucket,
        runGcsPath = resultsDir
    )

fun FileReference.uploadIfNeeded(
    rootGcsBucket: String,
    runGcsPath: String
): FileReference = if (remote.isNotBlank()) this else copy(remote = upload(local, rootGcsBucket, runGcsPath))

fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String {
    return uploadToRemoteStorage(
        RemoteStorage.Dir(rootGcsBucket, runGcsPath),
        RemoteStorage.Data(file, Files.readAllBytes(Paths.get(file)))
    )
}

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
