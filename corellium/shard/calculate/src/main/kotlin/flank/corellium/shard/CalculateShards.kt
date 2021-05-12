package flank.corellium.shard

/**
 * Distribute the test cases into the [List] of shards where each shard have similar duration.
 * @receiver The bunch of test cases grouped by test and app.
 * @return [List] of shards where each shard may contains many apps and test cases.
 */
fun Apps.calculateShards(
    maxCount: Int
): Shards = this
    .mapToInternalChunks()
    .groupByDuration(maxCount)
    .mapToShards()
