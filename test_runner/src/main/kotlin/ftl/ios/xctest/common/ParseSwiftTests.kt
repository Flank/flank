package ftl.ios.xctest.common

import ftl.config.FtlConstants.isMacOS
import ftl.util.Bash

internal fun parseSwiftTests(binary: String): List<String> {
    installBinaries
    validateIsFile(binary)
    val results = mutableListOf<String>()
    // The OS limits the list of arguments to ARG_MAX. Setting the xargs limit avoids a fatal
    // 'argument too long' error. xargs will split the args and run the command for each chunk.
    // getconf ARG_MAX
    val argMax = 262_144

    val cmd = if (isMacOS) {
        "nm -gU ${binary.quote()} | xargs -s $argMax xcrun swift-demangle"
    } else {
        "export LD_LIBRARY_PATH=~/.flank; export PATH=~/.flank:\$PATH; nm -gU ${binary.quote()} | xargs -s $argMax swift-demangle"
    }

    // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L17-18
    val demangledOutput = Bash.execute(cmd)
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
