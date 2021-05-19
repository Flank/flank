package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid

/**
 * The final step, notifies that execution completes without exceptions.
 */
internal fun finish() = RunTestCorelliumAndroid.step {
    println("* Finish")
    this
}
