package com.github.flank.wrapper.internal

internal fun runFlankOnLatestVersion(args: Array<out String>) {
    runCatching {
        setupCrashReporter()
        if (!compareLatestVersionCheckSumWithCurrent()) {
            println("The new Flank version is available. Downloading new version...")
            downloadLatestFlankVersion()
            println("The new Flank version is ready to use")
        }
        executeFlank(args)
    }.onFailure(Throwable::report)
}
