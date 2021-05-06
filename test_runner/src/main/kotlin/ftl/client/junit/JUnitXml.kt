package ftl.client.junit

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ftl.run.exception.FlankGeneralError
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private val xmlModule = JacksonXmlModule().apply { setDefaultUseWrapper(false) }

private val xmlMapper = XmlMapper(xmlModule)
    .configure(EMPTY_ELEMENT_AS_NULL, true)
    .registerModules(KotlinModule())
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

internal val xmlPrettyWriter = xmlMapper.writerWithDefaultPrettyPrinter()

private fun xmlText(path: Path): String {
    if (!path.toFile().exists()) throw FlankGeneralError("$path doesn't exist!")
    return String(Files.readAllBytes(path))
}

fun parseOneSuiteXml(path: File): JUnitTestResult {
    return parseOneSuiteXml(xmlText(path.toPath()))
}

fun parseOneSuiteXml(path: Path): JUnitTestResult {
    return parseOneSuiteXml(xmlText(path))
}

private fun parseOneSuiteXml(data: String): JUnitTestResult {
    return JUnitTestResult(mutableListOf(xmlMapper.readValue(fixHtmlCodes(data), JUnitTestSuite::class.java)))
}

fun parseAllSuitesXml(path: Path): JUnitTestResult {
    return parseAllSuitesXml(xmlText(path))
}

fun parseAllSuitesXml(path: File): JUnitTestResult {
    return parseAllSuitesXml(path.toPath())
}

private fun parseAllSuitesXml(data: String): JUnitTestResult =
    // This is workaround for flank being unable to parse <testsuites/> into JUnitTesResults
    // We need to preserve configure(EMPTY_ELEMENT_AS_NULL, true) to skip empty elements
    // Once better solution is found, this should be fixed
    if (data.contains("<testsuites/>"))
        JUnitTestResult(null)
    else
        xmlMapper.readValue(fixHtmlCodes(data), JUnitTestResult::class.java)
