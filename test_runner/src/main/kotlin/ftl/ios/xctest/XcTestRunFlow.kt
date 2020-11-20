package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.args.IosArgs
import ftl.ios.xctest.common.XcTestRunVersion.V1
import ftl.ios.xctest.common.XcTestRunVersion.V2
import ftl.ios.xctest.common.toByteArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

fun IosArgs.xcTestRunFlow(): Flow<ByteArray> =
// Parameterized tests on iOS don't shard correctly.
// Avoid changing Xctestrun file when disableSharding is on.
    if (disableSharding) flowOf(xcTestRunData.nsDict.toByteArray())
    else xcTestRunData.xcTestRunFlow()

private fun XcTestRunData.xcTestRunFlow(
    reduce: ReduceXCTestRun = createReducer()
): Flow<ByteArray> =
    flow {
        shardTargets.forEach { (configName, shards: List<Map<String, List<String>>>) ->
            shards.forEach { targets: Map<String, List<String>> ->
                nsDict.clone()
                    .reduce(configName, targets)
                    .toByteArray()
                    .let { emit(it) }
            }
        }
    }

private typealias ReduceXCTestRun =
    NSDictionary.(
        config: String,
        targets: Map<String, List<String>>
    ) -> NSDictionary

private fun XcTestRunData.createReducer(): ReduceXCTestRun =
    when (version) {
        V2 -> NSDictionary::reduceXcTestRunV2
        V1 -> { _, tests -> reduceXcTestRunV1(tests) }
    }
