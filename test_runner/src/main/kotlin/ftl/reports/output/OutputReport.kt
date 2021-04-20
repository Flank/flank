package ftl.reports.output

import ftl.api.RemoteStorage
import ftl.api.uploadToRemoteStorage
import ftl.run.common.prettyPrint
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

var outputReport = OutputReport()
    private set

data class OutputReport(
    val outputData: OutputData = mapOf(),
    val configuration: OutputReportConfiguration = OutputReportConfiguration()
)

private typealias OutputData = Map<String, Any>

fun OutputReport.configure(reportConfiguration: OutputReportConfiguration) {
    outputReport = copy(configuration = reportConfiguration)
}

fun OutputReport.add(key: String, reportNode: Any) {
    if (configuration.enabled) {
        outputReport = copy(outputData = outputData + (key to reportNode))
    }
}

fun OutputReport.generate() {
    val (resultsDirectory, outputFile) = configuration.local
    val (resultsBucket, resultsDir) = configuration.remote
    storeOutputData(resultsDirectory, outputFile)
        ?.takeIf { configuration.remote.uploadReport }
        ?.uploadToGcloud(resultsBucket, resultsDir)
}

private fun OutputReport.storeOutputData(
    resultsDirectory: String,
    outputFile: String
) = when (configuration.type) {
    OutputReportType.JSON ->
        outputData
            .takeIf { configuration.enabled }
            ?.toJson()
            ?.storeToFile(resultsDirectory, outputFile)
    OutputReportType.NONE -> null
}

private fun OutputData.toJson(): String = prettyPrint.toJson(this)

private fun String.storeToFile(
    directoryPath: String,
    fileName: String
) = File(directoryPath).let { directory ->
    if (directory.exists().not())
        directory.mkdirs()
    Paths.get(directoryPath, fileName).toFile().apply { writeText(this@storeToFile) }
}

private fun File.uploadToGcloud(resultsBucket: String, resultsDir: String) {
    upload(absolutePath, resultsBucket, resultsDir)
}

fun upload(file: String, rootGcsBucket: String, runGcsPath: String): String =
    uploadToRemoteStorage(
        RemoteStorage.Dir(rootGcsBucket, runGcsPath),
        RemoteStorage.Data(file, Files.readAllBytes(Paths.get(file)))
    )
