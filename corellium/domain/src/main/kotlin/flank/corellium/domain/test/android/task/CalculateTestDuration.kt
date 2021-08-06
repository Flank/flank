package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.TestDuration
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument.Status

val calculateTestDuration = TestDuration from setOf(
    ExecuteTests,
) using context {
    val (firstStatus, lastStatus) = testResult
        .flatMap { result -> result.value.filterIsInstance<Status>() }
        .run { minByOrNull(Status::startTime) to maxByOrNull(Status::endTime) }

    val startTime = firstStatus?.startTime ?: 0L
    val endTime = lastStatus?.endTime ?: 0L

    endTime - startTime
}
