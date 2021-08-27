package flank.json

import java.io.Writer

fun Any.writeJson(writer: Writer) = jsonPrettyWriter.writeValue(writer, this)
