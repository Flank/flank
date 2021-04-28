package flank.corellium.shard

/**
 * Distribute the test cases into the [List] of shards where each shard have similar duration.
 * @receiver The bunch of test cases grouped by test and app.
 * @return [List] of shards where each shard may contains many apps and test cases.
 */
fun List<Shard.App>.calculateShards(
    maxCount: Int
): List<List<Shard.App>> = this
    .mapToInternalChunks()
    .groupByDuration(maxCount)
    .mapToShards()

/**
 * Namespace for sharding input and output structures
 */
object Shard {

    class App(
        val name: String,
        val tests: List<Test>
    )

    class Test(
        val name: String,
        val cases: List<Case>
    ) {
        class Case(
            val name: String,
            val duration: Long = DEFAULT_DURATION
        )
    }

    private const val DEFAULT_DURATION = 120L
}
