package com.github.flank.wrapper.internal

import flank.common.downloadFile

internal fun downloadLatestFlankVersion() {
    downloadFile(
        sourceUrl = FLANK_JAR_REMOTE_PATH,
        destination = flankRunnerPath
    )
    sendAnalyticsNewFlankVersionDownloaded()
}

private const val FLANK_JAR_REMOTE_PATH = "https://github.com/Flank/flank/releases/latest/download/flank.jar"
