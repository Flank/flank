package ftl.gc

import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.ClientInfoDetail
import com.google.api.services.testing.model.ManualSharding
import com.google.api.services.testing.model.ShardingOption
import com.google.api.services.testing.model.TestTargetsForShard
import com.google.api.services.testing.model.UniformSharding
import ftl.args.AndroidArgs
import ftl.args.ShardChunks

internal fun Map<String, String>.toClientInfoDetailList() = map { (key, value) ->
    ClientInfoDetail()
        .setKey(key)
        .setValue(value)
}

internal fun AndroidInstrumentationTest.setupTestTargets(args: AndroidArgs, testShards: ShardChunks) = apply {
    if (args.disableSharding) {
        testTargets = testShards.flatten()
    } else {
        shardingOption = ShardingOption().apply {
            if (args.numUniformShards != null) {
                testTargets = testShards.flatten()
                uniformSharding = UniformSharding().setNumShards(args.numUniformShards)
            } else {
                manualSharding = ManualSharding().setTestTargetsForShard(testShards.map {
                    TestTargetsForShard().setTestTargets(it)
                })
            }
        }
    }
}
