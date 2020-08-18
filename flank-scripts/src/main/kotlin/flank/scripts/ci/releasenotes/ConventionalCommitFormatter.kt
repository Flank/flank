package flank.scripts.ci.releasenotes

import flank.scripts.utils.markdownBold

fun String.mapPrTitle() = when {
    startsWith("feat") -> replaceFirst("feat", "New feature".markdownBold())
    startsWith("fix") -> replaceFirst("fix", "Fix".markdownBold())
    startsWith("docs") -> replaceFirst("docs", "Documentation".markdownBold())
    startsWith("refactor") -> replaceFirst("refactor", "Refactor".markdownBold())
    startsWith("ci") -> replaceFirst("ci", "CI changes".markdownBold())
    startsWith("test") -> replaceFirst("test", "Tests update".markdownBold())
    startsWith("perf") -> replaceFirst("perf", "Performance upgrade".markdownBold())
    else -> null // we do not accept other prefix to have update in release notes
}
