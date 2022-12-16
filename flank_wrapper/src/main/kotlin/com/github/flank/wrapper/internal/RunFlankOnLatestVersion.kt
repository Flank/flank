package com.github.flank.wrapper.internal

internal fun runFlankOnLatestVersion(args: Array<out String>) {
    if (!compareLatestVersionCheckSumWithCurrent()) {
        println("The new Flank version is available. Downloading new version...")
        downloadLatestFlankVersion()
        println("The new Flank version is ready to use")
    }
    executeFlank(args)
}
