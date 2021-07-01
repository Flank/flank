package com.github.flank.wrapper.internal

import kotlin.system.exitProcess

fun runFlank(args: Array<out String>) {
    ProcessBuilder().command(
        "java", "-jar", flankRunnerPath, *args
    )
        .inheritIO()
        .start()
        .waitFor()
        .let { flankExitCode -> exitProcess(flankExitCode) }
}
