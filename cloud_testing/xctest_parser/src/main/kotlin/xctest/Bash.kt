package xctest

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

object Bash {

    private fun Process.stdout(): String {
        return BufferedReader(InputStreamReader(this.inputStream)).lines()
                .parallel().collect(Collectors.joining("\n"))
    }

    private fun Process.successful(): Boolean {
        return this.exitValue() == 0
    }

    fun execute(cmd: String): String {
        println(cmd)
        val process = Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", cmd))
        process.waitFor()
        val output = process.stdout()
        if (!process.successful()) throw RuntimeException("Command failed: $cmd")
        return output
    }
}
