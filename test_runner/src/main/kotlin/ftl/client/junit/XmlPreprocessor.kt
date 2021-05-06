package ftl.client.junit

import ftl.client.junit.UtfControlCharRanges.CONTROL_BOTTOM_END
import ftl.client.junit.UtfControlCharRanges.CONTROL_BOTTOM_START
import ftl.client.junit.UtfControlCharRanges.CONTROL_TOP_END
import ftl.client.junit.UtfControlCharRanges.CONTROL_TOP_START
import org.apache.commons.text.StringEscapeUtils

fun fixHtmlCodes(data: String): String = listOf(
    CONTROL_TOP_START.charValue..CONTROL_TOP_END.charValue,
    CONTROL_BOTTOM_START.charValue..CONTROL_BOTTOM_END.charValue
).flatten()
    .map { StringEscapeUtils.escapeXml11(it.toChar().toString()) }
    .filter { it.startsWith("&#") }
    .fold(data) { fixedStr: String, isoControlCode: String -> fixedStr.replace(isoControlCode, "") }

/**
 *   Numbers come from ascii table https://www.utf8-chartable.de.
 *   and represents control chars. We need to avoid characters in ranges CONTROL_TOP_START..CONTROL_TOP_END and
 *   CONTROL_BOTTOM_START..CONTROL_BOTTOM_END because chars from that range escaped to html causing parsing errors.
 * */
private enum class UtfControlCharRanges(val charValue: Int) {
    CONTROL_TOP_START(0x00),
    CONTROL_TOP_END(0x1F),
    CONTROL_BOTTOM_START(0x7F),
    CONTROL_BOTTOM_END(0x9F)
}
