package ftl.reports.xml

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private val xmlModule = JacksonXmlModule().apply { setDefaultUseWrapper(false) }
private val xmlMapper = XmlMapper(xmlModule)
    .registerModules(KotlinModule())
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

private val xmlPrettyWriter = xmlMapper.writerWithDefaultPrettyPrinter()

private fun xmlBytes(path: Path): ByteArray {
    if (!path.toFile().exists()) RuntimeException("$path doesn't exist!")
    return Files.readAllBytes(path)
}

fun JUnitTestResult?.xmlToString(): String {
    if (this == null) return ""
    val prefix = "<?xml version='1.0' encoding='UTF-8' ?>\n"
    return prefix + xmlPrettyWriter.writeValueAsString(this)
}

fun parseOneSuiteXml(bytes: ByteArray): JUnitTestResult {
    return JUnitTestResult(mutableListOf(xmlMapper.readValue(bytes, JUnitTestSuite::class.java)))
}

fun parseOneSuiteXml(path: Path): JUnitTestResult {
    return parseOneSuiteXml(xmlBytes(path))
}

fun parseOneSuiteXml(path: File): JUnitTestResult {
    return parseOneSuiteXml(xmlBytes(path.toPath()))
}

fun parseOneSuiteXml(data: String): JUnitTestResult {
    return parseOneSuiteXml(data.toByteArray())
}

// --

fun parseAllSuitesXml(bytes: ByteArray): JUnitTestResult {
    return xmlMapper.readValue(bytes, JUnitTestResult::class.java)
}

fun parseAllSuitesXml(path: Path): JUnitTestResult {
    return parseAllSuitesXml(xmlBytes(path))
}

fun parseAllSuitesXml(path: File): JUnitTestResult {
    return parseAllSuitesXml(path.toPath())
}

fun parseAllSuitesXml(data: String): JUnitTestResult {
    return parseAllSuitesXml(data.toByteArray())
}
