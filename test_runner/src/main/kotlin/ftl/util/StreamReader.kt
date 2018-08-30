package ftl.util

import java.io.InputStream

class StreamReader(private val inputStream: InputStream) : Thread() {
    var text: String = ""

    override fun run() {
        // .use is required to close the input stream https://stackoverflow.com/a/39500046
        text = inputStream.bufferedReader().use { it.readText() }
    }
}
