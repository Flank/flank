package flank.instrument.command

/**
 * The helper function for formatting instrumentation test shell command:
 * ```
 * $ adb am instrument -r -w -e "testCases[0..n]" "$packageName/$testRunner"
 * ```
 *
 * Test cases names should match the following format:
 * ```
 * "class package.name.ClassName#methodName"
 * ```
 * or
 * ```
 * "package package.name"
 * ```
 *
 * @param packageName the package name of the android test apk.
 * @param testRunner the test runner full name.
 * @param testCases the list of test cases names.
 */
fun formatAmInstrumentCommand(
    packageName: String,
    testRunner: String,
    testCases: List<String>,
): String {
    val testCasesChunk = testCases // example: listOf("class foo.Bar#baz")
        .map { it.split(" ") } // example: listOf(listOf("class", "foo.Bar#baz"))
        .groupBy({ it.first() }, { it.last() }) // example: first => "class", last => "foo.Bar#baz"
        .toList().joinToString("") { (type, tests: List<String>) ->
            "-e $type ${tests.joinToString(",")} " // example: "-e class foo.Bar#baz"
        } // example: "-e class foo.Bar#baz1,foo.Bar#baz2 -e package foo.test "

    val runnerChunk = "$packageName/$testRunner"

    // example: "am instrument -r -w -e class foo.Bar#baz foo.test/androidx.test.runner.AndroidJUnitRunner"
    return AM_INSTRUMENT + testCasesChunk + runnerChunk
}

private const val AM_INSTRUMENT = "am instrument -r -w "
