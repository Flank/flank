package flank.scripts.utils

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

fun <T> T.toJson(serializationStrategy: SerializationStrategy<T>) =
    Json.encodeToString(serializationStrategy, this)

fun <T> String.toObject(deserializationStrategy: DeserializationStrategy<T>) =
    Json.decodeFromString(deserializationStrategy, this)
