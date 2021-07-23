package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.CompleteTests
import flank.corellium.domain.TestAndroid.DumpShards
import flank.corellium.domain.TestAndroid.GenerateReport
import flank.exection.parallel.from
import flank.exection.parallel.using

/**
 * The final task, notifies that execution completes without exceptions.
 */
internal val finish = CompleteTests from setOf(
    GenerateReport,
    DumpShards
) using { }
