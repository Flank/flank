package flank.corellium.shard

import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal val prettyGson: Gson = GsonBuilder().setPrettyPrinting().create()
