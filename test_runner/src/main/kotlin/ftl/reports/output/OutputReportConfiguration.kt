package ftl.reports.output

import ftl.args.IArgs
import ftl.args.localStorageDirectory

data class OutputReportConfiguration(
    val enabled: Boolean = false,
    val local: OutputReportLocalConfiguration = OutputReportLocalConfiguration(),
    val remote: OutputReportRemoteConfiguration = OutputReportRemoteConfiguration(),
)

data class OutputReportLocalConfiguration(
    val storageDirectory: String = "",
    val outputFile: String = "outputReport.json"
)

data class OutputReportRemoteConfiguration(
    val resultsBucket: String = "",
    val resultsDir: String = "",
    val uploadReport: Boolean = false
)

fun IArgs.toOutputReportConfiguration() = OutputReportConfiguration(
    enabled = enableOutputReport,
    local = OutputReportLocalConfiguration(localStorageDirectory),
    remote = OutputReportRemoteConfiguration(
        uploadReport = disableResultsUpload.not(),
        resultsBucket = resultsBucket,
        resultsDir = resultsDir
    )
)
