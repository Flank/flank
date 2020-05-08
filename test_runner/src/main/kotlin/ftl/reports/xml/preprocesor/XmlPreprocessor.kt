package ftl.reports.xml.preprocesor

import org.apache.commons.text.StringEscapeUtils

fun fixHtmlCodes(data: String): String = listOf(
    UtfControlCharRanges.CONTROL_TOP_START.charValue..UtfControlCharRanges.CONTROL_TOP_END.charValue,
    UtfControlCharRanges.CONTROL_BOTTOM_START.charValue..UtfControlCharRanges.CONTROL_BOTTOM_END.charValue
).flatten()
    .map { StringEscapeUtils.escapeXml11(it.toChar().toString()) }
    .filter { it.startsWith("&#") }
    .fold(data) { fixedStr: String, isoControlCode: String -> fixedStr.replace(isoControlCode, "") }
