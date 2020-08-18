package flank.scripts.utils

fun String.markdownBold() = "**$this**"

fun markdownLink(description: String, url: String) = "[$description]($url)"

fun String.markdownH2() = "## $this"
