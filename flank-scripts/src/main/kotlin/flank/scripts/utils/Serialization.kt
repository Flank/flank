package flank.scripts.utils

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

private val json by lazy {
    Json {
        ignoreUnknownKeys = true
    }
}

fun <T> T.toJson(serializationStrategy: SerializationStrategy<T>) =
    json.encodeToString(serializationStrategy, this)

fun <T> String.toObject(deserializationStrategy: DeserializationStrategy<T>) =
    json.decodeFromString(deserializationStrategy, this)
