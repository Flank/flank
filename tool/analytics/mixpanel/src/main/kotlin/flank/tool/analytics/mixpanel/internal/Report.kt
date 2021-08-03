package flank.tool.analytics.mixpanel.internal

import flank.tool.analytics.mixpanel.internal.send.sendConfiguration
import kotlin.reflect.KClass

internal object Report {
    var projectName: String = ""
    var blockSendUsageStatistics: Boolean = false
    var classesForStatistics: List<KClass<*>>? = null
    val data: MutableMap<String, Any> = mutableMapOf()
}

internal fun initStatisticsClient(blockUsageStatistics: Boolean, statisticClasses: Array<out KClass<*>>) {
    // TODO Verify if condition is needed
    if (Report.classesForStatistics != null) return
    Report.blockSendUsageStatistics = blockUsageStatistics
    Report.classesForStatistics = statisticClasses.asList()
}

internal fun configureReport(
    projectName: String,
    blockUsageStatistics: Boolean,
    statisticClasses: Array<out KClass<*>>
) {
    Report.projectName = projectName
    Report.blockSendUsageStatistics = blockUsageStatistics
    Report.classesForStatistics = statisticClasses.asList()
}

internal fun addToReport(key: String, reportNode: Any) {
    Report.data[key] = reportNode
}

internal fun sendReport(eventName: String) {
    sendConfiguration(Report.projectName, Report.data, eventName)
}
