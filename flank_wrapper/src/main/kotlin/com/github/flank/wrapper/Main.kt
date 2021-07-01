package com.github.flank.wrapper

import com.github.flank.wrapper.internal.compareLatestVersionCheckSumWithCurrent
import com.github.flank.wrapper.internal.downloadLatestFlankVersion
import com.github.flank.wrapper.internal.runFlank

fun main(vararg args: String) {
    if (!compareLatestVersionCheckSumWithCurrent()) {
        downloadLatestFlankVersion()
    }
    runFlank(args)
}
