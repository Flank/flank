package ftl.ios

import ftl.config.FtlConstants.macOS
import ftl.util.Bash
import ftl.util.Utils.copyBinaryResource
import java.io.File

object Parse {

    init {
        if (!macOS) {
            copyBinaryResource("nm")
            copyBinaryResource("swift-demangle")
            copyBinaryResource("libatomic.so.1") // swift-demangle dependency
            copyBinaryResource("libatomic.so.1.2.0")
        }
    }

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

    internal fun parseObjcTests(binary: String): List<String> {
        validateFile(binary)

        val results = mutableListOf<String>()
        // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L18
        var cmd = "nm -U $binary"
        if (!macOS) cmd = "PATH=~/.flank $cmd"
        val output = Bash.execute(cmd)

        output.lines().forEach { line ->
            // 000089b0 t -[EarlGreyExampleTests testLayout]
            // 00008330 t -[EarlGreyExampleTests testCustomAction]
            val pattern = """.+\st\s-\[(.+\stest.+)]""".toRegex()
            val matcher = pattern.find(line)
            if (matcher != null && matcher.groupValues.size == 2) {
                results.add(methodName(matcher))
            }
        }
        return results.distinct()
    }

    internal fun parseSwiftTests(binary: String): List<String> {
        validateFile(binary)
        val results = mutableListOf<String>()
        // The OS limits the list of arguments to ARG_MAX. Setting the xargs limit avoids a fatal
        // 'argument too long' error. xargs will split the args and run the command for each chunk.
        // getconf ARG_MAX
        val argMax = 262_144

        val cmd = if (macOS) {
            "nm -gU $binary | xargs -s $argMax xcrun swift-demangle"
        } else {
            "export LD_LIBRARY_PATH=~/.flank; export PATH=~/.flank:\$PATH; nm -gU $binary | xargs -s $argMax swift-demangle"
        }

        // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L17-18
        val demangledOutput = Bash.execute(cmd)
        demangledOutput.lines().forEach { line ->
            // _T025EarlGreyExampleTestsSwift0abceD0C10testLayoutyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testLayout() -> ()
            // _T025EarlGreyExampleTestsSwift0abceD0C16testCustomActionyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testCustomAction() -> ()
            val pattern = """.+\s--->\s.+\.(.+\.test.+)\(\)\s->\s\(\)""".toRegex()
            val matcher = pattern.find(line)
            if (matcher != null && matcher.groupValues.size == 2) {
                results.add(methodName(matcher))
            }
        }
        return results.distinct()
    }
}
