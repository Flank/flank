package ftl.reports.xml.preprocesor

import org.apache.commons.text.StringEscapeUtils

fun fixHtmlCodes(data: String): String {
    val isoHtmlCodesToReplace =
        listOf(UtfControlChars.CONTROL_TOP_START.charValue..UtfControlChars.CONTROL_TOP_END.charValue).union(
            listOf(UtfControlChars.CONTROL_BOTTOM_START.charValue..UtfControlChars.CONTROL_BOTTOM_END.charValue)
        ).flatten()
            .map { StringEscapeUtils.escapeXml11(it.toChar().toString()) }.filter { it.startsWith("&#") }

    var fixedStr = data
    for (isoControlCode in isoHtmlCodesToReplace) {
        fixedStr = fixedStr.replace(isoControlCode, "")
    }
    return fixedStr
}
