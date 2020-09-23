package flank.scripts.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json by lazy {
    Json {
        ignoreUnknownKeys = true
    }
}

internal inline fun <reified T> T.toJson() = json.encodeToString(this)

internal inline fun <reified T> String.toObject() = json.decodeFromString<T>(this)
