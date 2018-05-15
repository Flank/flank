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
        println("running command: $cmd")
        val process = Runtime.getRuntime().exec(cmd)
        process.waitFor()
        val output = process.stdout()
        val statusCode = process.successful()
        println("successful? $statusCode")
        // TODO: handle failures
        return output
    }

    internal fun parseObjcTests(file: File): Set<String> {
        if (!file.exists()) {
            throw RuntimeException("File $file does not exist!")
        }
        val results = mutableSetOf<String>()
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
        val mangledOutput = execute("nm -gU ${file.path}")
        val demangledOutput = execute("xcrun swift-demangle $mangledOutput")
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
        // TODO: Build sample app from source
        // TODO: Extract tests from sample app (Swift/ObjC)
        // TODO: Add unit tests

//        execute("./build_earlgrey_example.sh")

        // TODO: Replace hardcoded file with args
        val objcBinary = File("./src/test/kotlin/xctest/fixtures/objc/EarlGreyExampleTests")
        parseObjcTests(objcBinary)

        val swiftBinary = File("./src/test/kotlin/xctest/fixtures/swift/EarlGreyExampleSwiftTests")
        parseSwiftTests(swiftBinary)
    }
}
