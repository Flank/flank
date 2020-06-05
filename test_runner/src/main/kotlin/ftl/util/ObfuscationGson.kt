package ftl.util

import com.google.common.annotations.VisibleForTesting
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import ftl.args.ShardChunks
import java.lang.reflect.Type

val obfuscatePrettyPrinter = GsonBuilder()
    .registerTypeHierarchyAdapter(ListOfStringListTypeToken.rawType, ObfusctedIosJsonSerializer)
    .registerTypeAdapter(ListOfStringTypeToken.rawType, ObfusctedAndroidJsonSerializer)
    .setPrettyPrinting()
    .create()!!

@VisibleForTesting
internal object ListOfStringTypeToken : TypeToken<List<String>>()

@VisibleForTesting
internal object ListOfStringListTypeToken : TypeToken<ShardChunks>()

private object ObfusctedAndroidJsonSerializer : JsonSerializer<List<String>> {
    private val obfuscation by lazy { AndroidObfuscation() }

    override fun serialize(src: List<String>?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonArray().also { jsonArray ->
            src?.forEach {
                val items = it.split(" ") // split for class and test name
                jsonArray.add(items.first() + " " + obfuscation.obfuscate(items.last()))
            }
        }
    }
}

private object ObfusctedIosJsonSerializer : JsonSerializer<List<List<String>>> {
    private val obfuscation by lazy { IosObfuscation() }

    override fun serialize(
        src: List<List<String>>?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonArray().also { jsonArray ->
            src?.forEach { list ->
                jsonArray.add(JsonArray().also { nestedJsonArray ->
                    list.forEach { nestedJsonArray.add(obfuscation.obfuscate(it)) }
                })
            }
        }
    }
}
