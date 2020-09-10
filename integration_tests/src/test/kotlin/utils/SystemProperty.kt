package utils

import java.util.*

fun Map<String,String>.getPropertyAsList(key: String, default: List<String>) = get(key).orEmpty().run {
    if (isBlank()) default
    else split(",")
}

fun Properties.toStringMap() = map { Pair(it.key.toString(), it.value.toString()) }.toMap()
