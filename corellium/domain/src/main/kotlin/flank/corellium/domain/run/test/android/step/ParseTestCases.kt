package flank.corellium.domain.run.test.android.step

import flank.apk.Apk
import flank.corellium.domain.RunTestCorelliumAndroid.ParseTestCases
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.using
import flank.filter.createTestCasesFilter

/**
 * The step is parsing the test methods from each test apk.
 */
internal val parseTestCasesFromApks = ParseTestCases using context {
    val config = Apk.ParseTestCases.Config(
        filterTest = createTestCasesFilter(args.testTargets),
        filterClass = setOf(Apk.Runner.JUnitParamsRunner, Apk.Runner.Parameterized)
    )
    args.apks
        // Get the list of paths to test apks
        .flatMap { app -> app.tests.map { test -> test.path } }
        // Associate parsed test methods to each apk path
        .associateWith(apk.parseTestCases(config))
}
