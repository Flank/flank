package com.github.flank.wrapper.internal

import flank.common.downloadFile
import java.io.File
import java.security.MessageDigest
import kotlin.io.path.createTempFile
import kotlin.io.path.readText

// https://github.com/Flank/flank/releases/latest/download/flank.sha256
fun compareLatestVersionCheckSumWithCurrent(): Boolean {
    val file = createTempFile("flank", "sha256")
    downloadFile(
        sourceUrl = "https://github.com/Flank/flank/releases/latest/download/flank.sha256",
        destinationPath = file.toAbsolutePath()
    )

    val flankFile = File(flankRunnerPath)

    return flankFile.exists() && flankFile.sha256() == file.readText()
}

private fun File.sha256(): String = readText().sha256()

private fun String.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: String, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}
