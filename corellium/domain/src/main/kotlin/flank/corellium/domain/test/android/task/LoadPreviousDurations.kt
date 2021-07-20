package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Args.DefaultOutputDir.ROOT
import flank.corellium.domain.TestAndroid.DEFAULT_TEST_CASE_DURATION
import flank.corellium.domain.TestAndroid.LoadPreviousDurations
import flank.corellium.domain.TestAndroid.ParseTestCases
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.junit.calculateTestCaseDurations
import flank.junit.mergeTestCases

/**
 * The step is searching result directory for JUnitReport.xml.
 * Collected reports are used for calculating test cases durations.
 *
 * For test cases represented by their class, the duration will be based on summary durations from methods of this class.
 * Basically, this is necessary for parameterized tests that are treated as class instead of method.
 */
internal val loadPreviousDurations = LoadPreviousDurations from setOf(
    ParseTestCases
) using context {
    val directoryToScan: String =
        if (args.outputDir.startsWith(ROOT)) ROOT
        else args.outputDir

    val classCases: Set<String> =
        testCases.values.flatten().filter { '#' !in it }.toSet()

    junit.parseTestResults(directoryToScan)
        .take(args.scanPreviousDurations).toList()
        .apply { LoadPreviousDurations.Searching(size).out() }
        .map { suite -> suite.mergeTestCases(classCases) }
        .flatten()
        .calculateTestCaseDurations()
        .withDefault { DEFAULT_TEST_CASE_DURATION }
        .apply { printStats(keys).out() }
}

private fun TestAndroid.Context.printStats(obtainedDurations: Set<String>): LoadPreviousDurations.Summary {
    val testCasesNames = testCases.flatMap { (_, cases) -> cases }.toSet()
    val unknown: Int = (obtainedDurations - testCasesNames).size
    val matching: Int = obtainedDurations.size - unknown
    val required: Int = testCasesNames.size

    return LoadPreviousDurations.Summary(unknown, matching, required)
}
