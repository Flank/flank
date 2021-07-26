package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.InvokeDevices
import flank.corellium.domain.TestAndroid.TestExecution
import flank.corellium.domain.TestAndroid.TestExecution.Results.Rerun
import flank.corellium.domain.TestAndroid.TestExecution.Results.Shard
import flank.exection.parallel.from
import flank.exection.parallel.plus
import flank.exection.parallel.select
import flank.exection.parallel.using

/**
 * Specifies all tasks required to fulfill execution based on dispatch queue.
 */
internal val testExecution = TestExecution from setOf(
    InvokeDevices,
    Dispatch.Tests,
    Dispatch.Failed,
) using {
    select(Dispatch.Tests).let { list: List<TestAndroid.Device.Result> ->
        mapOf(
            Shard + list.filter { result -> result.data.type == Dispatch.Type.Shard },
            Rerun + list.filter { result -> result.data.type == Dispatch.Type.Rerun },
        )
    }
}
