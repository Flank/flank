package ftl.reports.xml.preprocesor

import org.apache.commons.text.StringEscapeUtils

fun fixHtmlCodes(data: String): String {
    val isoHtmlCodesToReplace = listOf(0x00..0x1F).union(listOf(0x7F..0x9F)).flatten()
        .map { StringEscapeUtils.escapeXml11(it.toChar().toString()) }.filter { it.startsWith("&#") }

    var fixedStr = data
    for (isoControlCode in isoHtmlCodesToReplace) {
        fixedStr = fixedStr.replace(isoControlCode, "")
    }
    return fixedStr
}