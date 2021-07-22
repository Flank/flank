package flank.tool.analytics.mixpanel

const val schemaVersion = "1.0"

var analyticsReport = AnalyticsReport()
    private set

data class AnalyticsReport(
    val projectName: String = "",
    val data: AnalyticsData = mapOf()
)

private typealias AnalyticsData = Map<String, Any>

fun AnalyticsReport.configure(projectName: String) {
    analyticsReport = copy(projectName = projectName)
}

fun AnalyticsReport.add(key: String, reportNode: Any) = also {
    if (blockSendUsageStatistics.not()) {
        analyticsReport = copy(data = analyticsReport.data + (key to reportNode))
    }
}

fun AnalyticsReport.send() = sendConfiguration(analyticsReport.projectName, data)
