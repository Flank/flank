package flank.tool.analytics.mixpanel.internal

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import flank.tool.analytics.mixpanel.ObjectMap

internal fun Any.objectToMap(): ObjectMap =
    objectMapper.convertValue(this, object : TypeReference<ObjectMap>() {})

private val objectMapper by lazy { jsonMapper { addModule(kotlinModule()) } }
