package flank.corellium.api

/**
 * The configuration of test plan for android tests executions.
 */
object AndroidTestPlan {

    /**
     * The configuration of the Android test plan.
     *
     * @property instances the map of instance ids with related shards.
     */
    data class Config(
        val instances: Map<InstanceId, List<Shard>>
    )

    /**
     * The configuration data for the shell command:
     * ```
     * $ adb am instrument -r -w -e "testCases[0..n]" "$packageName/$testRunner"
     * ```
     *
     * Test cases names should match the following format:
     * ```
     * "class package.name.ClassName#methodName"
     * ```
     *
     * @property packageName the package name of the android test apk.
     * @property testRunner the test runner full name.
     * @property testCases the list of test cases names.
     */
    data class Shard(
        val packageName: String,
        val testRunner: String,
        val testCases: List<String>
    )

    /**
     * Execute tests on android instances using specified configuration.
     */
    interface Execute : Request<Config, Unit>
}

private typealias InstanceId = String
