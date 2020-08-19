package flank.scripts.ci.releasenotes

import flank.scripts.utils.markdownH2
import flank.scripts.utils.markdownH3
import java.lang.StringBuilder

typealias ReleaseNotesWithType = Map<String, List<String>>

fun ReleaseNotesWithType.asString(headerTag: String) =
    StringBuilder(headerTag.markdownH2())
        .appendln()
        .apply {
            this@asString.forEach { (type, messages) ->
                appendln(type.markdownH3())
                messages.forEach { appendln(it) }
            }
        }
        .toString()
