package ftl.reports.xml

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private val xmlModule = JacksonXmlModule().apply { setDefaultUseWrapper(false) }
private val xmlMapper = XmlMapper(xmlModule)
    .registerModules(KotlinModule())
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
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

fun parseAndroidXml(path: Path): JUnitTestResult {
    return JUnitTestResult(mutableListOf(xmlMapper.readValue(xmlBytes(path), JUnitTestSuite::class.java)))
}

fun parseAndroidXml(path: File): JUnitTestResult {
    return parseAndroidXml(path.toPath())
}

fun parseIosXml(path: Path): JUnitTestResult {
    return xmlMapper.readValue(xmlBytes(path), JUnitTestResult::class.java)
}

fun parseIosXml(path: File): JUnitTestResult {
    return parseIosXml(path.toPath())
}
