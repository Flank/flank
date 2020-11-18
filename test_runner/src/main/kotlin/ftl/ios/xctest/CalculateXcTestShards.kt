package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.args.ArgsHelper.calculateShards
import ftl.args.IosArgs
import ftl.ios.xctest.common.XcTestRunVersion
import ftl.ios.xctest.common.XcTestRunVersion.V1
import ftl.ios.xctest.common.XcTestRunVersion.V2
import ftl.ios.xctest.common.getXcTestRunVersion
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.ios.xctest.common.toByteArray
import ftl.run.exception.FlankConfigurationError
import ftl.shard.Chunk
import ftl.shard.testCases
import ftl.util.FlankTestMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

data class XcTestRunData(
    val version: XcTestRunVersion,
    val rootDir: String,
    val nsDict: NSDictionary,
    val shards: Map<String, Map<String, List<Chunk>>>
)

fun IosArgs.calculateXcTestRunData(): XcTestRunData {
    val iosArgs = this
    val xcTestRunFile = File(xctestrunFile)
    val xcTestRoot: String = xcTestRunFile.parent + "/"
    val xcTestNsDictionary: NSDictionary = parseToNSDictionary(xcTestRunFile)
    val xcTestVersion = xcTestNsDictionary.getXcTestRunVersion()
    val regexList = testTargets.mapToRegex()

    val calculatedShards: Map<String, Map<String, List<Chunk>>> =
        if (disableSharding) emptyMap()
        else {
            val configurations: Map<String, Map<String, List<String>>> =
                when (xcTestVersion) {
                    V1 -> mapOf("" to findXcTestNamesV1(xcTestRoot, xcTestNsDictionary))
                    V2 -> findXcTestNamesV2(xcTestRoot, xcTestNsDictionary)
                }
            val filteredMethods: Map<String, Map<String, List<FlankTestMethod>>> =
                configurations.mapValues { (_, targets) ->
                    targets.mapValues { (_, methods) ->
                        methods
                            .filterAnyMatches(regexList)
                            .map(::FlankTestMethod)
                    }
                }
            filteredMethods.mapValues { (_, targets) ->
                targets.mapValues { (_, methods) ->
                    calculateShards(methods, iosArgs).shardChunks
                }
            }
        }

    return XcTestRunData(
        version = xcTestVersion,
        rootDir = xcTestRoot,
        nsDict = xcTestNsDictionary,
        shards = calculatedShards
    )
}

private fun List<String>.mapToRegex(): List<Regex> = map { filter ->
    try {
        filter.toRegex()
    } catch (e: Exception) {
        throw FlankConfigurationError("Invalid regex: $filter", e)
    }
}

private fun List<String>.filterAnyMatches(
    list: List<Regex>
): List<String> =
    if (list.isEmpty()) this
    else filter { test -> list.any { regex -> regex.matches(test) } }

fun XcTestRunData.flattenShardChunks() =
    shards.values.flatMap { it.values }.flatten()

fun IosArgs.xcTestRunFlow(): Flow<ByteArray> {
    val reduce = xcTestRunData.createReducer()
    return flow {
        // Parameterized tests on iOS don't shard correctly.
        // Avoid changing Xctestrun file when disableSharding is on.
        if (disableSharding) emit(xcTestRunData.nsDict.toByteArray())
        else xcTestRunData.shards.forEach { (configName, targets: Map<String, List<Chunk>>) ->
            targets.forEach { (targetName, shards) ->
                shards.testCases.forEach { methods ->
                    xcTestRunData.nsDict
                        .clone()
                        .reduce(configName, targetName, methods)
                        .toByteArray()
                        .let { emit(it) }
                }
            }
        }
    }
}

private fun XcTestRunData.createReducer(): NSDictionary.(String, String, List<String>) -> NSDictionary =
    when (version) {
        V2 -> NSDictionary::reduceXcTestRunV2
        V1 -> { _, testTarget, methods ->
            reduceXcTestRunV1(testTarget, methods)
        }
    }
