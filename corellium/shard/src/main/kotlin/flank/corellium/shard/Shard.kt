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
 * Namespace for sharding input and output structures.
 */
object Shard {

    /**
     * Abstract representation for test app apk.
     * @property name An abstract name for identifying app.
     * @property tests The list of tests modules related to app.
     */
    class App(
        val name: String,
        val tests: List<Test>
    )

    /**
     * Abstract representation for test apk.
     * @property name An abstract name for identifying test group.
     * @property cases The list of test cases related to group.
     */
    class Test(
        val name: String,
        val cases: List<Case>
    ) {
        /**
         * Abstract representation for test case (test method).
         * @property name An abstract name for identifying test case.
         * @property duration The duration of the test case run. Use default if no previous duration was recorded.
         */
        class Case(
            val name: String,
            val duration: Long = DEFAULT_DURATION
        )
    }

    private const val DEFAULT_DURATION = 120L
}
