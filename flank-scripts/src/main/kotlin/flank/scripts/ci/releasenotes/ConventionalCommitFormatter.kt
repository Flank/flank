package flank.scripts.ci.releasenotes

fun String.mapPrTitle() = when {
    startsWith("feat") -> replaceFirst("feat", "New feature".bold())
    startsWith("fix") -> replaceFirst("fix", "Fix".bold())
    startsWith("docs") -> replaceFirst("docs", "Documentation".bold())
    startsWith("refactor") -> replaceFirst("refactor", "Refactor".bold())
    startsWith("ci") -> replaceFirst("ci", "CI changes".bold())
    startsWith("test") -> replaceFirst("test", "Tests update".bold())
    startsWith("perf") -> replaceFirst("perf", "Performance upgrade".bold())
    startsWith("style") -> null
    startsWith("build") -> null
    startsWith("chore") -> null
    else -> null
}

private fun String.bold() = "**$this**"
