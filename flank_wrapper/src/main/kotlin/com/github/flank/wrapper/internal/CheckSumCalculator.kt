package com.github.flank.wrapper.internal

import java.io.File
import java.security.MessageDigest

internal fun File.sha256(): String = readBytes().sha256()

private fun ByteArray.sha256(): String {
    return hashString(this, "SHA-256")
}

private fun hashString(input: ByteArray, algorithm: String): String {
    return MessageDigest
        .getInstance(algorithm)
        .digest(input)
        .fold("") { str, it -> str + "%02x".format(it) }
}
