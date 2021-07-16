package com.github.flank.wrapper.internal

import kotlin.system.exitProcess

internal fun executeFlank(args: Array<out String>) {
    sendAnalyticsFlankRun()

    ProcessBuilder()
        .command(buildRunCommand(args))
        .inheritIO()
        .start()
        .waitFor()
        .let { flankExitCode -> exitProcess(flankExitCode) }
}

internal fun buildRunCommand(args: Array<out String>): List<String> = listOf(
    "java",
    "-jar",
    flankRunnerPath
) + args.asList()
