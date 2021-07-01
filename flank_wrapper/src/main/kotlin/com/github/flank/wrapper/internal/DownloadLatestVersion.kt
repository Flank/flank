package com.github.flank.wrapper.internal

import flank.common.downloadFile

// https://github.com/Flank/flank/releases/latest/download/flank.jar
fun downloadLatestFlankVersion() {
    downloadFile(
        sourceUrl = FLANK_JAR_REMOTE_PATH,
        destination = flankRunnerPath
    )
}

private const val FLANK_JAR_REMOTE_PATH = "https://github.com/Flank/flank/releases/latest/download/flank.jar"
