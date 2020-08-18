package flank.scripts.utils

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

private val json by lazy { Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true)) }

fun <T> T.toJson(serializationStrategy: SerializationStrategy<T>) = json.stringify(serializationStrategy, this)

fun <T> String.toObject(deserializationStrategy: DeserializationStrategy<T>) = json.parse(deserializationStrategy, this)
