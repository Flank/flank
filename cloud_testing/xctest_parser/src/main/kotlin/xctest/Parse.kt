package xctest

import java.io.File

object Parse {

    private fun validateFile(path: String) {
        val file = File(path)
        if (!file.exists()) {
            throw RuntimeException("File $path does not exist!")
        }

        if (file.isDirectory) throw RuntimeException("$path is a directory!")
    }

    private fun methodName(matcher: MatchResult): String {
        return matcher.groupValues.last()
                .replace('.', '/')
                .replace(' ', '/')
    }

    internal fun parseObjcTests(binary: String): Set<String> {
        validateFile(binary)

        val results = mutableSetOf<String>()
        // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L18
        val output = Bash.execute("nm -U $binary")
        output.lines().forEach { line ->
            // 000089b0 t -[EarlGreyExampleTests testLayout]
            // 00008330 t -[EarlGreyExampleTests testCustomAction]
            val pattern = """.+\st\s-\[(.+\stest.+)]""".toRegex()
            val matcher = pattern.find(line)
            if (matcher != null && matcher.groupValues.size == 2) {
                results.add(methodName(matcher))
            }
        }
        return results
    }

    internal fun parseSwiftTests(binary: String): Set<String> {
        validateFile(binary)

        val results = mutableSetOf<String>()

        // The OS limits the list of arguments to ARG_MAX. Setting the xargs limit avoids a fatal
        // 'argument too long' error. xargs will split the args and run the command for each chunk.
        val argMax = Bash.execute("getconf ARG_MAX")
        // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L17-18
        val demangledOutput = Bash.execute("nm -gU $binary | xargs -s $argMax xcrun swift-demangle")
        demangledOutput.lines().forEach { line ->
            // _T025EarlGreyExampleTestsSwift0abceD0C10testLayoutyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testLayout() -> ()
            // _T025EarlGreyExampleTestsSwift0abceD0C16testCustomActionyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testCustomAction() -> ()
            val pattern = """.+\s--->\s.+\.(.+\.test.+)\(\)\s->\s\(\)""".toRegex()
            val matcher = pattern.find(line)
            if (matcher != null && matcher.groupValues.size == 2) {
                results.add(methodName(matcher))
            }
        }
        return results
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // TODO: Replace hardcoded file with args
        val objcBinary = "./src/test/kotlin/xctest/fixtures/objc/EarlGreyExampleTests"
        parseObjcTests(objcBinary)

        val swiftBinary = "./src/test/kotlin/xctest/fixtures/swift/EarlGreyExampleSwiftTests"
        parseSwiftTests(swiftBinary)
    }
}
