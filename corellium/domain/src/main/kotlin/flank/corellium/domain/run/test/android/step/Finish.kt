package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.CompleteTests
import flank.corellium.domain.step

/**
 * The final step, notifies that execution completes without exceptions.
 */
internal fun RunTestCorelliumAndroid.Context.finish() = step(CompleteTests) { this }
