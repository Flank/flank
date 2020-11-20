package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.args.IosArgs
import ftl.ios.xctest.common.XcTestRunVersion.V1
import ftl.ios.xctest.common.XcTestRunVersion.V2
import ftl.ios.xctest.common.toByteArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun IosArgs.xcTestRunFlow(
    reduce: ReduceXCTestRun = xcTestRunData.createReducer()
): Flow<ByteArray> =
    flow {
        // Parameterized tests on iOS don't shard correctly.
        // Avoid changing Xctestrun file when disableSharding is on.
        if (disableSharding) emit(xcTestRunData.nsDict.toByteArray())
        else xcTestRunData.shardTargets.forEach { (configName, shards: List<Map<String, List<String>>>) ->
            shards.forEach { targets ->
                targets.forEach { (targetName, methods) ->
                    xcTestRunData.nsDict.clone()
                        .reduce(configName, targetName, methods)
                        .toByteArray()
                        .let { emit(it) }
                }
            }
        }
    }

private typealias ReduceXCTestRun =
    NSDictionary.(
        config: String,
        target: String,
        methods: List<String>
    ) -> NSDictionary

private fun XcTestRunData.createReducer(): ReduceXCTestRun =
    when (version) {
        V2 -> NSDictionary::reduceXcTestRunV2
        V1 -> { _, testTarget, methods ->
            reduceXcTestRunV1(testTarget, methods)
        }
    }
