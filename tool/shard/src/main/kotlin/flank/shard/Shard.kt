package flank.shard

/**
 * Namespace for sharding input and output structures.
 */
object Shard {

    /**
     * Abstract representation for test app apk.
     * @property name An abstract name for identifying app.
     * @property tests The list of tests modules related to app.
     */
    data class App(
        val name: String,
        val tests: List<Test>
    )

    /**
     * Abstract representation for test apk.
     * @property name An abstract name for identifying test group.
     * @property cases The list of test cases related to group.
     */
    data class Test(
        val name: String,
        val cases: List<Case>
    ) {
        /**
         * Abstract representation for test case (test method).
         * @property name An abstract name for identifying test case.
         * @property duration The duration of the test case run in milliseconds. Use default if no previous duration was recorded.
         */
        data class Case(
            val name: String,
            val duration: Long = DEFAULT_DURATION
        )
    }

    private const val DEFAULT_DURATION = 120L
}

/**
 * Collection of apps, tests, and test cases that can be identified as test-shard for running on a single device instance.
 */
typealias InstanceShard = List<Shard.App>

/**
 * List of shards. Each shard for another device instance.
 */
typealias Shards = List<InstanceShard>

/**
 * Collection of apps, tests, and test cases that can be used to calculate [Shards]
 */
typealias Apps = List<Shard.App>
