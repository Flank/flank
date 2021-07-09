package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.LoadPreviousDurations
import flank.corellium.domain.RunTestCorelliumAndroid.ParseTestCases
import flank.corellium.domain.RunTestCorelliumAndroid.PrepareShards
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.shard.Shard
import flank.shard.calculateShards

/**
 * The task is calculating shard data.
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
    apks: List<RunTestCorelliumAndroid.Args.Apk.App>,
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
