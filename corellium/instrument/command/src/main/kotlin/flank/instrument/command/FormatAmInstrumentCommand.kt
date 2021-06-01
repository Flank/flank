package flank.instrument.command

/**
 * The helper function for formatting instrumentation test shell command:
 * ```
 * $ adb shell am instrument -r -w -e "testCases[0..n]" "$packageName/$testRunner"
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
 * @param packageName The package name of the android test apk.
 * @param testRunner The test runner full name.
 * @param testCases The list of test cases names.
 * @param noWindowAnimation Adds `--no-window-animation` to command.
 */
fun formatAmInstrumentCommand(
    packageName: String,
    testRunner: String,
    testCases: List<String>,
    noWindowAnimation: Boolean = true,
): String {
    val testCasesChunk = testCases // example: listOf("class foo.Bar#baz")
        .map { it.split(" ") } // example: listOf(listOf("class", "foo.Bar#baz"))
        .groupBy({ it.first() }, { it.last() }) // example: first => "class", last => "foo.Bar#baz"
        .toList().joinToString("") { (type, tests: List<String>) ->
            "-e $type ${tests.joinToString(",")} " // example: "-e class foo.Bar#baz"
        }.trimEnd() // example: "-e class foo.Bar#baz1,foo.Bar#baz2 -e package foo.test"

    val runnerChunk = "$packageName/$testRunner"

    // example: "am instrument -r -w --no-window-animation -e class foo.Bar#baz foo.test/androidx.test.runner.AndroidJUnitRunner"
    return listOfNotNull(
        AM_INSTRUMENT,
        NO_WINDOW_ANIMATION.takeIf { noWindowAnimation },
        testCasesChunk,
        runnerChunk
    ).joinToString(" ")
}

private const val AM_INSTRUMENT = "am instrument -r -w"
private const val NO_WINDOW_ANIMATION = "--no-window-animation"
