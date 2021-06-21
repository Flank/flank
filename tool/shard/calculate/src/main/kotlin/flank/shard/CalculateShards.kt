package flank.shard

/**
 * Distribute the test cases into the [List] of shards where each shard have similar duration.
 * @receiver The bunch of test cases grouped by test and app.
 * @return [List] of shards where each shard may contains many apps and test cases.
 */
fun calculateShards(
    apps: Apps,
    maxCount: Int
): Shards = apps
    .mapToInternalChunks()
    .groupByDuration(maxCount)
    .mapToShards()
