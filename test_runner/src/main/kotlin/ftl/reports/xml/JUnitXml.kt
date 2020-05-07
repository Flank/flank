package ftl.reports.xml

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.preprocesor.fixHtmlCodes
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private val xmlModule = JacksonXmlModule().apply { setDefaultUseWrapper(false) }

private val xmlMapper = XmlMapper(xmlModule)
    .registerModules(KotlinModule())
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

internal val xmlPrettyWriter = xmlMapper.writerWithDefaultPrettyPrinter()

private fun xmlText(path: Path): String {
    if (!path.toFile().exists()) throw RuntimeException("$path doesn't exist!")
    return String(Files.readAllBytes(path))
}

fun JUnitTestResult?.xmlToString(): String {
    if (this == null) return ""
    val prefix = "<?xml version='1.0' encoding='UTF-8' ?>\n"
    return prefix + xmlPrettyWriter.writeValueAsString(this)
}

fun parseOneSuiteXml(path: Path): JUnitTestResult {
    return parseOneSuiteXml(xmlText(path))
}

fun parseOneSuiteXml(path: File): JUnitTestResult {
    return parseOneSuiteXml(xmlText(path.toPath()))
}

fun parseOneSuiteXml(data: String): JUnitTestResult {
    return JUnitTestResult(mutableListOf(xmlMapper.readValue(fixHtmlCodes(data), JUnitTestSuite::class.java)))
}

// --

fun parseAllSuitesXml(path: Path): JUnitTestResult {
    return parseAllSuitesXml(xmlText(path))
}

fun parseAllSuitesXml(path: File): JUnitTestResult {
    return parseAllSuitesXml(path.toPath())
}

fun parseAllSuitesXml(data: String): JUnitTestResult {
    return xmlMapper.readValue(fixHtmlCodes(data), JUnitTestResult::class.java)
}
