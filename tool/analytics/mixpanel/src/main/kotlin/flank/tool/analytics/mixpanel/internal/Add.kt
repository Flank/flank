package flank.tool.analytics.mixpanel.internal

internal fun addToReport(key: String, reportNode: Any) {
    Report.data[key] = reportNode
}
