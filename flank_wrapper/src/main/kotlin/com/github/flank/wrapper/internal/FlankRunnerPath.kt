package com.github.flank.wrapper.internal

import flank.common.dotFlank

internal val flankRunnerPath: String
    get() = "$dotFlank/$FLANK_JAR_NAME"

private const val FLANK_JAR_NAME = "flank.jar"
