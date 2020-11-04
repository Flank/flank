package ftl.ios.xctest.common

import ftl.config.FtlConstants
import ftl.util.Bash

internal fun parseObjcTests(binary: String): List<String> {
    installBinaries
    validateIsFile(binary)

    val results = mutableListOf<String>()
    // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L18
    // must quote binary path in case there are spaces
    var cmd = "nm -U ${binary.quote()}"
    if (!FtlConstants.isMacOS) cmd = "PATH=~/.flank $cmd"
    val output = Bash.execute(cmd)

    output.lines().forEach { line ->
        // 000089b0 t -[EarlGreyExampleTests testLayout]
        // 00008330 t -[EarlGreyExampleTests testCustomAction]
        val pattern = """.+\st\s-\[(.+\stest.+)]""".toRegex()
        val matcher = pattern.find(line)
        if (matcher != null && matcher.groupValues.size == 2) {
            results.add(parseXcMethodName(matcher))
        }
    }
    return results.distinct()
}
