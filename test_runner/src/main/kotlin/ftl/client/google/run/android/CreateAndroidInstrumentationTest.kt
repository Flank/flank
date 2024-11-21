package ftl.client.google.run.android

import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.ManualSharding
import com.google.api.services.testing.model.ShardingOption
import com.google.api.services.testing.model.TestTargetsForShard
import com.google.api.services.testing.model.UniformSharding
import flank.common.logLn
import ftl.api.ShardChunks
import ftl.api.TestMatrixAndroid

internal fun createAndroidInstrumentationTest(
    config: TestMatrixAndroid.Type.Instrumentation
) = AndroidInstrumentationTest().apply {
    appApk = FileReference().setGcsPath(config.appApkGcsPath)
    testApk = FileReference().setGcsPath(config.testApkGcsPath)
    testRunnerClass = config.testRunnerClass
    orchestratorOption = config.orchestratorOption
}.setupTestTargets(
    disableSharding = config.disableSharding,
    testShards = config.testShards,
    numUniformShards = config.numUniformShards,
    keepTestTargetsEmpty = config.keepTestTargetsEmpty,
    testTargetsForShard = config.testTargetsForShard
)

internal fun AndroidInstrumentationTest.setupTestTargets(
    disableSharding: Boolean,
    testShards: ShardChunks,
    numUniformShards: Int?,
    keepTestTargetsEmpty: Boolean,
    testTargetsForShard: ShardChunks
) = apply {
    when {
        keepTestTargetsEmpty -> {
            testTargets = emptyList()
        }
        disableSharding -> {
            testTargets = testShards.flatten()
        }
        else -> {
            shardingOption = ShardingOption().apply {
                if (numUniformShards != null) {
                    testTargets = testShards.flatten()
                    val safeNumUniformShards = if (testTargets.size > numUniformShards) numUniformShards else {
                        logLn("WARNING: num-uniform-shards ($numUniformShards) is greater than number of test cases (${testTargets.size}) from ${testApk.gcsPath}")
                        testTargets.size
                    }
                    uniformSharding = UniformSharding().setNumShards(safeNumUniformShards)
                } else {
                    manualSharding = createManualSharding(testTargetsForShard, testShards)
                }
            }
        }
    }
}

private fun createManualSharding(testFlagForShard: ShardChunks, testShards: ShardChunks) =
    if (testFlagForShard.isNotEmpty()) {
        ManualSharding().setTestTargetsForShard(
            testFlagForShard.map {
                TestTargetsForShard().setTestTargets(it)
            }
        )
    } else {
        ManualSharding().setTestTargetsForShard(
            testShards.map {
                TestTargetsForShard().setTestTargets(it)
            }
        )
    }
