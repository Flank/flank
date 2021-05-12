package flank.junit.mapper

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import flank.junit.JUnit
import java.io.File
import java.util.Locale

internal val xmlModule = JacksonXmlModule().apply { setDefaultUseWrapper(false) }

internal val objectMapper = XmlMapper(xmlModule)
    .apply {
        configure(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL, true)
        configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
    }
    .registerModules(KotlinModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

internal val xmlPrettyWriter = objectMapper.writerWithDefaultPrettyPrinter()

internal class TimeSerializer : JsonSerializer<Double>() {
    override fun serialize(value: Double, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(
            if (value == 0.0) "0.0"
            else String.format(Locale.US, "%.3f", value)
        )
    }
}

/**
 * For <testsuites/> objectMapper is returning null.
 * This is helper method for fixing this behaviour.
 */
internal fun File.readEmptyTestSuites(): JUnit.Report? =
    JUnit.Report().takeIf {
        readLines()
            .filter { it.isNotBlank() }
            .run { size < 3 && last() == "<testsuites/>" }
    }
