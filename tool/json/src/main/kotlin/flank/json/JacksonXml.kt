package flank.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.util.Locale

private val jsonFactory = JsonFactory()

internal val jsonMapper = JsonMapper(jsonFactory)
    .registerModules(KotlinModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

internal val jsonPrettyWriter = jsonMapper.writerWithDefaultPrettyPrinter()

internal class TimeSerializer : JsonSerializer<Double>() {
    override fun serialize(value: Double, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(
            if (value == 0.0) "0.0"
            else String.format(Locale.US, "%.3f", value)
        )
    }
}
