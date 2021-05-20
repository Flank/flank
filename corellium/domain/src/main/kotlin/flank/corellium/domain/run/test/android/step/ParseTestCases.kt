package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid

/**
 * The step is parsing the test methods from each test apk.
 *
 * updates:
 * * [RunTestCorelliumAndroid.State.testCases]
 */
internal fun RunTestCorelliumAndroid.Context.parseTestCasesFromApks() = RunTestCorelliumAndroid.step {
    println("* Parsing test cases")
    copy(
        testCases = args.apks
            // Get the list of paths to test apks
            .flatMap { app -> app.tests.map { test -> test.path } }
            // Associate parsed test methods to each apk path
            .associateWith(apk.parseTestCases)
    )
}
