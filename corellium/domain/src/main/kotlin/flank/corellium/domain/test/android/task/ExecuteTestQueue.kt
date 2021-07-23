package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.InvokeDevices
import flank.exection.parallel.from
import flank.exection.parallel.select
import flank.exection.parallel.using

/**
 * Specifies all tasks required to fulfill execution based on dispatch queue.
 */
internal val executeTestQueue = ExecuteTests from setOf(
    InvokeDevices,
    Dispatch.Tests,
    Dispatch.Failed,
) using {
    select(Dispatch.Tests)
}
