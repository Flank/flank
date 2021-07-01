package com.github.flank.wrapper.internal

import flank.common.downloadFile
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.io.path.readText

internal fun compareLatestVersionCheckSumWithCurrent(): Boolean = getFlankLocalChecksum() == getFlankRemoteChecksum()

private fun getFlankLocalChecksum(): String {
    val flankFile = File(flankRunnerPath)
    return flankFile.takeIf(File::exists)?.let(File::sha256).orEmpty()
}

private fun getFlankRemoteChecksum(): String = with(createTempFile("flank", "sha256")) {
    downloadFile(
        sourceUrl = "https://github.com/Flank/flank/releases/latest/download/flank.sha256",
        destinationPath = toAbsolutePath()
    )
    readText().split("\\s+".toRegex()).first()
}
