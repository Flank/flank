package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.LoadPreviousDurations
import flank.corellium.domain.TestAndroid.ParseTestCases
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.shard.Shard
import flank.shard.calculateShards

/**
 * Calculates shard data.
 */
internal val prepareShards = PrepareShards from setOf(
    ParseTestCases,
    LoadPreviousDurations,
) using context {
    calculateShards(
        apps = prepareDataForSharding(
            apks = args.apks,
            testCases = testCases,
            durations = previousDurations,
        ),
        maxCount = args.maxShardsCount
    )
}

/**
 * Prepare input data for sharding calculation.
 * It's a simple data mapping, no API calls or logical operations.
 */
private fun prepareDataForSharding(
    apks: List<TestAndroid.Args.Apk.App>,
    testCases: Map<String, List<String>>,
    durations: Map<String, Long>,
): List<Shard.App> =
    apks.map { app ->
        Shard.App(
            name = app.path,
            tests = app.tests.map { test ->
                Shard.Test(
                    name = test.path,
                    cases = testCases
                        .getValue(test.path)
                        .map { name ->
                            Shard.Test.Case(
                                name = name,
                                duration = durations.getValue(name)
                            )
                        }
                )
            }
        )
    }
