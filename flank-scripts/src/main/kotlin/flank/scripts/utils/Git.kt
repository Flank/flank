package flank.scripts.utils

fun currentGitBranch(): String = "git branch --show-current".runForOutput()
    .trim().takeIf(String::isNotBlank)
    ?: getEnv("HEAD_REF")
