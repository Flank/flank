package flank.tool.analytics.mixpanel.internal

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

internal fun Any.objectToMap(): Map<String, Any> =
    objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})

private val objectMapper by lazy { jsonMapper { addModule(kotlinModule()) } }
