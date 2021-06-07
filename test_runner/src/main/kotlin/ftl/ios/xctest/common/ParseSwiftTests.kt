package ftl.ios.xctest.common

import flank.common.appDataDirectory
import flank.common.isLinux
import flank.common.isMacOS
import flank.common.isWindows
import ftl.util.Bash
import ftl.util.ShellEnvironment

internal fun parseSwiftTests(binary: String): List<String> {
    installBinaries
    validateIsFile(binary)
    val results = mutableListOf<String>()
    // The OS limits the list of arguments to ARG_MAX. Setting the xargs limit avoids a fatal
    // 'argument too long' error. xargs will split the args and run the command for each chunk.
    // getconf ARG_MAX
    // Windows has different limits
    val argMax = if (isWindows) 251_36 else 262_144

    val cmd = when {
        isMacOS -> "nm -gU ${binary.quote()} | xargs -s $argMax xcrun swift-demangle"
        isLinux -> "export LD_LIBRARY_PATH=~/.flank; export PATH=~/.flank:\$PATH; nm -gU ${binary.quote()} | xargs -s $argMax swift-demangle"
        isWindows -> "llvm-nm.exe --undefined-only --extern-only ${binary.quote().replace("\\", "/")} | xargs.exe swift-demangle"
        else -> throw RuntimeException("Unsupported OS for Integration Tests")
    }

    val path = if (isWindows) {
        listOf(Pair("Path", "$appDataDirectory\\.flank\\;C:\\Windows\\System32\\"), Pair("LD_LIBRARY_PATH", "$appDataDirectory\\.flank\\"))
    } else emptyList()

    // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L17-18
    val shell = if (isWindows) ShellEnvironment.Cmd else ShellEnvironment.Default

    val demangledOutput = Bash.execute(cmd, path, shell)
    demangledOutput.lines().forEach { line ->
        // _T025EarlGreyExampleTestsSwift0abceD0C10testLayoutyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testLayout() -> ()
        // _T025EarlGreyExampleTestsSwift0abceD0C16testCustomActionyyF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testCustomAction() -> ()
        // _$S25EarlGreyExampleTestsSwift0abceD0C14testThatThrowsyyKF ---> EarlGreyExampleTestsSwift.EarlGreyExampleSwiftTests.testThatThrows() throws -> ()
        val pattern = """.+\s--->\s.+\.(.+\.test.+)\(\)\s.*->\s\(\)""".toRegex()
        val matcher = pattern.find(line)

        if (matcher != null && matcher.groupValues.size == 2) {
            results.add(parseXcMethodName(matcher))
        }
    }
    return results.distinct()
}
