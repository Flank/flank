package ftl.reports.xml.preprocesor

import org.apache.commons.text.StringEscapeUtils

fun fixHtmlCodes(data: String): String {
    val isoHtmlCodesToReplace =
        listOf(UtfControlCharRanges.CONTROL_TOP_START.charValue..UtfControlCharRanges.CONTROL_TOP_END.charValue).union(
            listOf(UtfControlCharRanges.CONTROL_BOTTOM_START.charValue..UtfControlCharRanges.CONTROL_BOTTOM_END.charValue)
        ).flatten()
            .map { StringEscapeUtils.escapeXml11(it.toChar().toString()) }.filter { it.startsWith("&#") }

    var fixedStr = data
    for (isoControlCode in isoHtmlCodesToReplace) {
        fixedStr = fixedStr.replace(isoControlCode, "")
    }
    return fixedStr
}
