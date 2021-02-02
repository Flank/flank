package ftl.reports.output

import ftl.gc.GcStorage
import ftl.run.common.prettyPrint
import java.io.File
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
    outputData
        .takeIf { configuration.enabled }
        ?.toJson()
        ?.storeToFile(resultsDirectory, outputFile)
        ?.takeIf { configuration.remote.uploadReport }
        ?.uploadToGcloud(resultsBucket, resultsDir)
}

private fun OutputData.toJson() = prettyPrint.toJson(this)

private fun String.storeToFile(
    directoryPath: String,
    fileName: String
) = Paths.get(directoryPath, fileName).toFile()
    .apply { writeText(this@storeToFile) }

private fun File.uploadToGcloud(resultsBucket: String, resultsDir: String) {
    GcStorage.upload(absolutePath, resultsBucket, resultsDir)
}
