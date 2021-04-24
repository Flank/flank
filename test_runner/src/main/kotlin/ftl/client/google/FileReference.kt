package ftl.client.google

import ftl.adapter.google.asFileReference
import ftl.api.FileReference
import ftl.api.RemoteStorage
import ftl.api.downloadFileReference
import ftl.api.uploadToRemoteStorage
import ftl.args.IArgs
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.parseAllSuitesXml
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

internal fun downloadAsJunitXml(args: IArgs): JUnitTestResult? =
    downloadFileReference(args.smartFlankGcsPath.asFileReference(), false, true)
        .local.takeIf { it.isNotEmpty() }
        ?.let { parseAllSuitesXml(Paths.get(it)) }

internal fun FileReference.downloadIfNeeded() = downloadFileReference(this, true, false)
