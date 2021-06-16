package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.ParseTestCases
import flank.corellium.domain.step

/**
 * The step is parsing the test methods from each test apk.
 *
 * updates:
 * * [RunTestCorelliumAndroid.State.testCases]
 */
internal fun RunTestCorelliumAndroid.Context.parseTestCasesFromApks() = step(ParseTestCases) {
    copy(
        testCases = args.apks
            // Get the list of paths to test apks
            .flatMap { app -> app.tests.map { test -> test.path } }
            // Associate parsed test methods to each apk path
            .associateWith(apk.parseTestCases)
    )
}
