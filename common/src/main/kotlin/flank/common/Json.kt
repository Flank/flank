package flank.common

import org.json.JSONObject

fun Map<*, *>.toJSONObject() = JSONObject(this)
