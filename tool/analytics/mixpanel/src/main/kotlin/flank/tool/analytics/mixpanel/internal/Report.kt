package flank.tool.analytics.mixpanel.internal

internal object Report {
    // Configuration
    var projectName: String = ""
    var blockSendUsageStatistics: Boolean = false
    var keysToRemove: Set<String> = emptySet()
    var keysToAnonymize: Set<String> = emptySet()

    // Accumulated data
    val data: MutableMap<String, Any> = mutableMapOf()
}
