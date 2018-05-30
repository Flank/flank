package xctest

import java.io.InputStream

class StreamGobbler(private val inputStream: InputStream) : Thread() {
    var output: String = ""

    override fun run() {
        // .use is required to close the input stream
        output = inputStream.bufferedReader().use { it.readText() }
    }
}

