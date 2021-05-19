package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid

/**
 * The step is cleaning instances from changes applied during test execution.
 */
internal fun RunTestCorelliumAndroid.Context.cleanUpInstances() = RunTestCorelliumAndroid.step {
    println("* Cleaning instances")
    println("TODO") // TODO
    this
}
