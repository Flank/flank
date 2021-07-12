package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.Args.DefaultOutputDir
import flank.corellium.domain.RunTestCorelliumAndroid.LoadPreviousDurations
import flank.corellium.domain.step
import flank.junit.calculateTestCaseDurations
import flank.junit.mergeTestCases

/**
 * The step is searching result directory for JUnitReport.xml.
 * Collected reports are used for calculating test cases durations.
 *
 * For test cases represented by their class, the duration will be based on summary durations from methods of this class.
 * Basically, this is necessary for parameterized tests that are treated as class instead of method.
 *
 *
 * require:
 * * [RunTestCorelliumAndroid.Context.parseTestCasesFromApks]
 *
 * updates:
 * * [RunTestCorelliumAndroid.State.previousDurations]
 */
internal fun RunTestCorelliumAndroid.Context.loadPreviousDurations() = step(LoadPreviousDurations) { out ->
    val directoryToScan: String =
        if (args.outputDir.startsWith(DefaultOutputDir.ROOT)) DefaultOutputDir.ROOT
        else args.outputDir

    val classCases: Set<String> =
        testCases.values.flatten().filter { '#' !in it }.toSet()

    copy(
        previousDurations = junit.parseTestResults(directoryToScan)
            .take(args.scanPreviousDurations).toList()
            .apply { LoadPreviousDurations.Searching(size).out() }
            .map { suite -> suite.mergeTestCases(classCases) }
            .flatten()
            .calculateTestCaseDurations()
            .withDefault { previousDurations.getValue(it) }
            .apply { printStats(keys).out() }
    )
}

private fun RunTestCorelliumAndroid.State.printStats(obtainedDurations: Set<String>): LoadPreviousDurations.Summary {
    val testCasesNames = testCases.flatMap { (_, cases) -> cases }.toSet()
    val unknown: Int = (obtainedDurations - testCasesNames).size
    val matching: Int = obtainedDurations.size - unknown
    val required: Int = testCasesNames.size

    return LoadPreviousDurations.Summary(unknown, matching, required)
}
