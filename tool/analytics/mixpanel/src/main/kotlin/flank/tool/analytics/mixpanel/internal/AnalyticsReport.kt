package flank.tool.analytics.mixpanel.internal

import kotlin.reflect.KClass

internal object AnalyticsReport {
    var projectName: String = ""
    var blockSendUsageStatistics: Boolean = false
    var classesForStatistics: List<KClass<*>>? = null
    val data: MutableMap<String, Any> = mutableMapOf()
}

internal fun initStatisticsClient(blockUsageStatistics: Boolean, statisticClasses: Array<out KClass<*>>) {
    if (AnalyticsReport.classesForStatistics != null) return
    AnalyticsReport.blockSendUsageStatistics = blockUsageStatistics
    AnalyticsReport.classesForStatistics = statisticClasses.asList()
}

internal fun configureReport(projectName: String) {
    AnalyticsReport.projectName = projectName
}

internal fun addToReport(key: String, reportNode: Any) {
    AnalyticsReport.data[key] = reportNode
}

internal fun sendReport(eventName: String) {
    sendConfiguration(AnalyticsReport.projectName, AnalyticsReport.data, eventName)
}
