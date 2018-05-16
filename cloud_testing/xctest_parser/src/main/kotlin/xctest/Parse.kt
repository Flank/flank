package xctest

import java.io.BufferedReader
import java.io.File
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

    private fun execute(cmd: String): String {
        println(cmd)
        val process = Runtime.getRuntime().exec(arrayOf("/bin/bash", "-c", cmd))
        process.waitFor()
        val output = process.stdout()
        if (!process.successful()) throw RuntimeException("Command failed: $cmd")
        return output
    }

    internal fun parseObjcTests(file: File): Set<String> {
        if (!file.exists()) {
            throw RuntimeException("File $file does not exist!")
        }
        val results = mutableSetOf<String>()
        // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L18
        val output = execute("nm -U ${file.path}")
        output.lines().forEach { line ->
            // 000089b0 t -[EarlGreyExampleTests testLayout]
            // 00008330 t -[EarlGreyExampleTests testCustomAction]
            val pattern = """.+\st\s-\[(.+\stest.+)]""".toRegex()
            val matcher = pattern.find(line)
            if (matcher != null && matcher.groupValues.size == 2) {
                results.add(matcher.groupValues.last())
            }
        }
        return results
    }

    internal fun parseSwiftTests(file: File): Set<String> {
        if (!file.exists()) {
            throw RuntimeException("File $file does not exist!")
        }
        val results = mutableSetOf<String>()

        // The OS limits the list of arguments to ARG_MAX. Setting the xargs limit avoids a fatal
        // 'argument too long' error. xargs will split the args and run the command for each chunk.
        val argMax = execute("getconf ARG_MAX")
        // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L17-18
        val demangledOutput = execute("nm -gU ${file.path} | xargs -s $argMax xcrun swift-demangle")
        demangledOutput.lines().forEach { line ->
            // _T025EarlGreyExampleTestsSwift0abceD0C10testLayoutyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testLayout() -> ()
            // _T025EarlGreyExampleTestsSwift0abceD0C16testCustomActionyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testCustomAction() -> ()
            val pattern = """.+\s--->\s.+\.(.+\.test.+)\(\)\s->\s\(\)""".toRegex()
            val matcher = pattern.find(line)
            if (matcher != null && matcher.groupValues.size == 2) {
                results.add(matcher.groupValues.last())
            }
        }
        return results
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // TODO: Replace hardcoded file with args
        val objcBinary = File("./src/test/kotlin/xctest/fixtures/objc/EarlGreyExampleTests")
        parseObjcTests(objcBinary)

        val swiftBinary = File("./src/test/kotlin/xctest/fixtures/swift/EarlGreyExampleSwiftTests")
        parseSwiftTests(swiftBinary)
    }
}
