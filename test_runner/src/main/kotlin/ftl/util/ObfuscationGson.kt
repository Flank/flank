package ftl.util

import com.google.common.annotations.VisibleForTesting
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

private typealias CustomShardChunks = List<Map<String, List<String>>>

val obfuscatePrettyPrinter = GsonBuilder()
    .registerTypeHierarchyAdapter(ListOfStringListTypeToken.rawType, ObfuscatedIosJsonSerializer)
    .registerTypeAdapter(ListOfStringTypeToken.rawType, ObfuscatedAndroidJsonSerializer)
    .setPrettyPrinting()
    .create()!!

@VisibleForTesting
internal object ListOfStringTypeToken : TypeToken<List<String>>()

@VisibleForTesting
internal object ListOfStringListTypeToken : TypeToken<CustomShardChunks>()

private object ObfuscatedAndroidJsonSerializer : JsonSerializer<List<String>> {
    private val obfuscationContext by lazy { mutableMapOf<String, MutableMap<String, String>>() }

    override fun serialize(
        src: List<String>,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ) = JsonArray().also { jsonArray ->
        src.forEach {
            val items = it.split(" ") // split for class and test name
            jsonArray.add(items.first() + " " + obfuscationContext.obfuscateAndroidTestName(items.last()))
        }
    }
}

private object ObfuscatedIosJsonSerializer : JsonSerializer<CustomShardChunks> {
    private val obfuscationContext by lazy { mutableMapOf<String, MutableMap<String, String>>() }

    override fun serialize(
        src: CustomShardChunks,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ) = JsonArray().also { jsonArray ->
        src.forEach { xcTestMethodList ->
            val xcTestMethodJson = JsonObject()
            xcTestMethodList.forEach {
                xcTestMethodJson.add(
                    obfuscationContext.obfuscateIosTestName(it.key),
                    JsonArray().also { nestedJsonArray ->
                        it.value.forEach {
                            nestedJsonArray.add(obfuscationContext.obfuscateIosTestName(it))
                        }
                    }
                )
            }
            jsonArray.add(xcTestMethodJson)
        }
    }
}
