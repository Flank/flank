package com.github.flank.wrapper.internal

import flank.analytics.send
import flank.analytics.toEvent

private const val FLANK_WRAPPER = "flank_wrapper"
private const val EVENT_RUN = "flank run"
private const val EVENT_NEW_FLANK_VERSION_DOWNLOADED = "new_version_downloaded"

internal fun sendAnalyticsNewFlankVersionDownloaded() {
    eventWithoutProperties(EVENT_NEW_FLANK_VERSION_DOWNLOADED).send()
}

internal fun sendAnalyticsFlankRun() {
    eventWithoutProperties(EVENT_RUN).send()
}

private fun eventWithoutProperties(
    eventName: String
) = emptyMap<String, Any>().toEvent(FLANK_WRAPPER, eventName)
