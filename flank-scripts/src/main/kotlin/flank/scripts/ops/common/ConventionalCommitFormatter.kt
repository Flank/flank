package flank.scripts.ops.common

fun String.mapPrTitleWithType() = when {
    startsWith("feat") -> "Features" to skipConventionalCommitPrefix().capitalize()
    startsWith("fix") -> "Bug Fixes" to skipConventionalCommitPrefix().capitalize()
    startsWith("docs") -> "Documentation" to skipConventionalCommitPrefix().capitalize()
    startsWith("refactor") -> "Refactor" to skipConventionalCommitPrefix().capitalize()
    startsWith("ci") -> "CI Changes" to skipConventionalCommitPrefix().capitalize()
    startsWith("test") -> "Tests update" to skipConventionalCommitPrefix().capitalize()
    startsWith("perf") -> "Performance upgrade" to skipConventionalCommitPrefix().capitalize()
    else -> null // we do not accept other prefix to have update in release notes
}

private fun String.skipConventionalCommitPrefix() = substring(indexOf(':') + 2)
