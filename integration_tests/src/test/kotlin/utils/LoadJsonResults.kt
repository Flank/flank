package utils

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

private val jsonMapper by lazy { JsonMapper().registerModule(KotlinModule()) }

fun File.loadJsonResults() = jsonMapper.readValue<Map<String, Any>>(this)
