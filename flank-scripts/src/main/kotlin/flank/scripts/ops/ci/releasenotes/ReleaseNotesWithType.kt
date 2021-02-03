package flank.scripts.ops.ci.releasenotes

import flank.scripts.utils.markdownH2
import flank.scripts.utils.markdownH3
import java.lang.StringBuilder

typealias ReleaseNotesWithType = Map<String, List<String>>

fun ReleaseNotesWithType.asString(headerTag: String) =
    StringBuilder(headerTag.markdownH2())
        .appendLine()
        .apply {
            this@asString.forEach { (type, messages) ->
                appendLine(type.markdownH3())
                messages.forEach { appendLine(it) }
            }
        }
        .toString()
