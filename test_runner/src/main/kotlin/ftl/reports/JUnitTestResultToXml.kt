package ftl.reports

import ftl.api.JUnitTest
import ftl.client.junit.xmlPrettyWriter

fun JUnitTest.Result?.toXmlString(): String {
    if (this == null) return ""
    val prefix = "<?xml version='1.0' encoding='UTF-8' ?>\n"
    return prefix + xmlPrettyWriter.writeValueAsString(this)
}
