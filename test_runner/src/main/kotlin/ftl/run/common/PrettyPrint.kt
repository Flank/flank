package ftl.run.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val prettyPrint: Gson = GsonBuilder().setPrettyPrinting().create()!!
