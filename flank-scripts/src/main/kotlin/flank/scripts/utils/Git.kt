package flank.scripts.utils

fun currentGitBranch(): String = "git branch --show-current".runForOutput()
    .trim()
    .takeIf(String::isNotBlank)
    ?: getFromEnvironmentVariableOrFallbackToMaster()

private fun getFromEnvironmentVariableOrFallbackToMaster() =
    runCatching { getEnv("HEAD_REF") }
        .getOrDefault("")
        .takeIf(String::isNotBlank)
        ?: "master"
