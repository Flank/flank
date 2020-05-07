package ftl.reports.xml.preprocesor

/**
 *   Numbers come from ascii table https://www.utf8-chartable.de.
 *   and represents control chars. We need to avoid characters in ranges CONTROL_TOP_START..CONTROL_TOP_END and
 *   CONTROL_BOTTOM_START..CONTROL_BOTTOM_END because chars from that range escaped to html causing parsing errors.
 * */
enum class UtfControlCharRanges(val charValue: Int) {
    CONTROL_TOP_START(0x00),
    CONTROL_TOP_END(0x1F),
    CONTROL_BOTTOM_START(0x7F),
    CONTROL_BOTTOM_END(0x9F)
}
