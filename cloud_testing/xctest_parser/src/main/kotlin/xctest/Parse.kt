package xctest

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

object Parse {

    private fun Process.stdout(): String {
        return BufferedReader(InputStreamReader(this.inputStream)).lines()
                .parallel().collect(Collectors.joining("\n"))
    }

    private fun Process.successful(): Boolean {
        return this.exitValue() == 0
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // TODO: Build sample app from source
        // TODO: Extract tests from sample app (Swift/ObjC)
        // TODO: Add unit tests

        val process = Runtime.getRuntime().exec("./build_earlgrey.sh")
        process.waitFor()
        println(process.stdout())
        println("successful? ${process.successful()}")
    }
}
