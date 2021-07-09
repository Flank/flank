package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid.CompleteTests
import flank.corellium.domain.RunTestCorelliumAndroid.DumpShards
import flank.corellium.domain.RunTestCorelliumAndroid.GenerateReport
import flank.exection.parallel.from
import flank.exection.parallel.using

/**
 * The final step, notifies that execution completes without exceptions.
 */
internal val finish = CompleteTests from setOf(
    GenerateReport,
    DumpShards
) using { }
