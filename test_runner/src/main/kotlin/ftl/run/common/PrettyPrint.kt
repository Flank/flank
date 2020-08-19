package ftl.run.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

val prettyPrint: Gson = GsonBuilder().setPrettyPrinting().create()

inline fun <reified T> fromJson(json: String): T {
    return prettyPrint.fromJson(json, object : TypeToken<T>() {}.type)
}
