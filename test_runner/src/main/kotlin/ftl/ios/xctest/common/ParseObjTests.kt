package ftl.ios.xctest.common

import flank.common.appDataDirectory
import flank.common.isMacOS
import flank.common.isWindows
import ftl.util.Bash

internal fun parseObjcTests(binary: String): List<String> {
    installBinaries
    validateIsFile(binary)

    val results = mutableListOf<String>()
    // https://github.com/linkedin/bluepill/blob/37e7efa42472222b81adaa0e88f2bd82aa289b44/Source/Shared/BPXCTestFile.m#L18
    // must quote binary path in case there are spaces
    var cmd = if (!isWindows) "nm -U ${binary.quote()}" else "llvm-nm.exe --undefined-only ${binary.quote()}"
    if (!isMacOS) cmd = if (isWindows) cmd.replace("\\", "/") else "PATH=~/.flank $cmd"

    val path = if (isWindows) listOf(Pair("Path", "$appDataDirectory\\.flank\\;C:\\Windows\\System32\\"))
    else emptyList()

    val output = Bash.execute(cmd, path)

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
