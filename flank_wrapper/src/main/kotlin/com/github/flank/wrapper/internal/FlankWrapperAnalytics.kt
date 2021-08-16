package com.github.flank.wrapper.internal

import flank.common.config.isTest
import flank.tool.analytics.mixpanel.Mixpanel

private const val FLANK_WRAPPER = "flank_wrapper"
private const val EVENT_RUN = "flank run"
private const val EVENT_NEW_FLANK_VERSION_DOWNLOADED = "new_version_downloaded"

internal fun sendAnalyticsNewFlankVersionDownloaded() {
    Mixpanel.configure(FLANK_WRAPPER, blockUsageStatistics = isTest())
    Mixpanel.add(FLANK_WRAPPER, EVENT_NEW_FLANK_VERSION_DOWNLOADED)
    Mixpanel.send(FLANK_WRAPPER)
}

internal fun sendAnalyticsFlankRun() {
    Mixpanel.configure(FLANK_WRAPPER, blockUsageStatistics = isTest())
    Mixpanel.add(FLANK_WRAPPER, EVENT_RUN)
    Mixpanel.send(FLANK_WRAPPER)
}
