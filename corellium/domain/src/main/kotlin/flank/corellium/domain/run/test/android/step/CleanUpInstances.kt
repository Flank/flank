package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.CleanUp
import flank.corellium.domain.step

/**
 * The step is cleaning instances from changes applied during test execution.
 */
internal fun RunTestCorelliumAndroid.Context.cleanUpInstances() = step(CleanUp) { out ->
    out("TODO")
    this
}
