package ftl.reports.xml

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import java.io.File
import java.nio.file.Path

private val xmlModule = JacksonXmlModule().apply { setDefaultUseWrapper(false) }
private val xmlMapper = XmlMapper(xmlModule)
    .registerModules(KotlinModule())
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

internal val xmlPrettyWriter = xmlMapper.writerWithDefaultPrettyPrinter()

fun JUnitTestResult?.xmlToString(): String {
    if (this == null) return ""
    val prefix = "<?xml version='1.0' encoding='UTF-8' ?>\n"
    return prefix + xmlPrettyWriter.writeValueAsString(this)
}

fun parseOneSuiteXml(path: Path): JUnitTestResult {
    return parseOneSuiteXml(path.toFile())
}

fun parseOneSuiteXml(file: File): JUnitTestResult {
    if (!file.exists()) throw RuntimeException("$file doesn't exist!")
    return JUnitTestResult(mutableListOf(xmlMapper.readValue(file, JUnitTestSuite::class.java)))
}

fun parseOneSuiteXml(data: String): JUnitTestResult {
    return JUnitTestResult(mutableListOf(xmlMapper.readValue(data, JUnitTestSuite::class.java)))
}

// --

fun parseAllSuitesXml(path: Path): JUnitTestResult {
    return parseAllSuitesXml(path.toFile())
}

fun parseAllSuitesXml(file: File): JUnitTestResult {
    if (!file.exists()) throw RuntimeException("$file doesn't exist!")
    return xmlMapper.readValue(file, JUnitTestResult::class.java)
}

fun parseAllSuitesXml(data: String): JUnitTestResult {
    return xmlMapper.readValue(data, JUnitTestResult::class.java)
}
