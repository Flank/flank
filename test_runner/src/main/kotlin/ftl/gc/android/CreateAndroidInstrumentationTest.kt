package ftl.gc.android

import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.ManualSharding
import com.google.api.services.testing.model.ShardingOption
import com.google.api.services.testing.model.TestTargetsForShard
import com.google.api.services.testing.model.UniformSharding
import ftl.args.ShardChunks
import ftl.run.platform.android.AndroidTestConfig

internal fun createAndroidInstrumentationTest(
    config: AndroidTestConfig.Instrumentation
) = AndroidInstrumentationTest().apply {
    appApk = FileReference().setGcsPath(config.appApkGcsPath)
    testApk = FileReference().setGcsPath(config.testApkGcsPath)
    testRunnerClass = config.testRunnerClass
    orchestratorOption = config.orchestratorOption
}.setupTestTargets(
    disableSharding = config.disableSharding,
    testShards = config.testShards,
    numUniformShards = config.numUniformShards
)

internal fun AndroidInstrumentationTest.setupTestTargets(
    disableSharding: Boolean,
    testShards: ShardChunks,
    numUniformShards: Int?
) = apply {
    if (disableSharding) {
        testTargets = testShards.flatten()
    } else {
        shardingOption = ShardingOption().apply {
            if (numUniformShards != null) {
                testTargets = testShards.flatten()
                val safeNumUniformShards = if (testTargets.size > numUniformShards) numUniformShards else {
                    println("WARNING: num-uniform-shards ($numUniformShards) is higher than number of test cases (${testTargets.size}) from ${testApk.gcsPath}")
                    testTargets.size
                }
                uniformSharding = UniformSharding().setNumShards(safeNumUniformShards)
            } else {
                manualSharding = ManualSharding().setTestTargetsForShard(testShards.map {
                    TestTargetsForShard().setTestTargets(it)
                })
            }
        }
    }
}
