package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.Args.DefaultOutputDir
import flank.junit.calculateTestCaseDurations

/**
 * The step is searching result directory for JUnitReport.xml.
 * Collected reports are used for calculating test cases durations.
 *
 * require:
 * * [RunTestCorelliumAndroid.Context.parseTestCasesFromApks]
 *
 * updates:
 * * [RunTestCorelliumAndroid.State.previousDurations]
 */
internal fun RunTestCorelliumAndroid.Context.loadPreviousDurations() = RunTestCorelliumAndroid.step {
    println("* Obtaining previous test cases durations")

    val directoryToScan: String =
        if (args.outputDir.startsWith(DefaultOutputDir.ROOT)) DefaultOutputDir.ROOT
        else args.outputDir

    copy(
        previousDurations = junit.parseTestResults(directoryToScan)
            .take(args.scanPreviousDurations).toList()
            .apply { println("Searching in $size JUnitReport.xml files...") }
            .flatten()
            .calculateTestCaseDurations()
            .withDefault { previousDurations.getValue(it) }
            .also { durations -> printStats(durations.keys) }
    )
}

private fun RunTestCorelliumAndroid.State.printStats(obtainedDurations: Set<String>) {
    val testCasesNames = testCases.flatMap { (_, cases) -> cases }.toSet()
    val unknown = (obtainedDurations - testCasesNames).size
    val matching = obtainedDurations.size - unknown
    val required = testCasesNames.size

    println("For $required test cases, found $matching matching and $unknown unknown")
}
