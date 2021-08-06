package flank.tool.analytics.mixpanel.internal

import flank.tool.analytics.mixpanel.ObjectMap

internal fun addToReport(key: String, reportNode: Any) {
    Report.data[key] = reportNode
}

internal fun addToReport(map: ObjectMap) {
    Report.data += map
}
