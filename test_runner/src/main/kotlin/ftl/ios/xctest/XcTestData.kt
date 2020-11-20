package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.args.ArgsHelper.calculateShards
import ftl.args.IosArgs
import ftl.ios.xctest.common.XcTestRunVersion
import ftl.ios.xctest.common.XcTestRunVersion.V1
import ftl.ios.xctest.common.XcTestRunVersion.V2
import ftl.ios.xctest.common.XctestrunMethods
import ftl.ios.xctest.common.getXcTestRunVersion
import ftl.ios.xctest.common.mapToRegex
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.shard.Chunk
import ftl.shard.testCases
import ftl.util.FlankTestMethod
import java.io.File

data class XcTestRunData(
    val version: XcTestRunVersion,
    val rootDir: String,
    val nsDict: NSDictionary,
    val shardTargets: Map<String, List<XctestrunMethods>>,
    val shardChunks: Map<String, List<Chunk>>
)

fun IosArgs.calculateXcTestRunData(): XcTestRunData {
    val xcTestRunFile = File(xctestrunFile)
    val xcTestRoot: String = xcTestRunFile.parent + "/"
    val xcTestNsDictionary: NSDictionary = parseToNSDictionary(xcTestRunFile)

    val calculatedShards: Map<String, Pair<List<Chunk>, List<XctestrunMethods>>> =
        if (disableSharding) emptyMap()
        else calculateXcTestRunShards(
            xcTestRoot = xcTestRoot,
            xcTestNsDictionary = xcTestNsDictionary,
            regexList = testTargets.mapToRegex()
        )

    return XcTestRunData(
        rootDir = xcTestRoot,
        nsDict = xcTestNsDictionary,
        version = xcTestNsDictionary.getXcTestRunVersion(),
        shardTargets = calculatedShards.mapValues { it.value.second },
        shardChunks = calculatedShards.mapValues { it.value.first }
    )
}

private fun IosArgs.calculateXcTestRunShards(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary,
    regexList: List<Regex>
): Map<String, Pair<List<Chunk>, List<XctestrunMethods>>> =
    findXcTestNames(
        xcTestRoot = xcTestRoot,
        xcTestNsDictionary = xcTestNsDictionary
    ).mapValues { (_, targets: Map<String, List<String>>) ->
        val calculatedShards = calculateShards(
            filteredTests = targets
                .values.flatten()
                .filterAnyMatches(regexList)
                .map(::FlankTestMethod),
            args = this
        ).shardChunks

        val testCases = calculatedShards.testCases

        calculatedShards to testCases.map { shardMethods ->
            targets.mapValues { (_, methods) ->
                methods.intersect(shardMethods).toList()
            }
        }
    }

private fun findXcTestNames(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary
): Map<String, Map<String, List<String>>> =
    when (xcTestNsDictionary.getXcTestRunVersion()) {
        V1 -> mapOf("" to findXcTestNamesV1(xcTestRoot, xcTestNsDictionary))
        V2 -> findXcTestNamesV2(xcTestRoot, xcTestNsDictionary)
    }

private fun List<String>.filterAnyMatches(
    list: List<Regex>
): List<String> =
    if (list.isEmpty()) this
    else filter { test -> list.any { regex -> regex.matches(test) } }

fun XcTestRunData.flattenShardChunks(): List<Chunk> =
    shardChunks.values.flatten()
