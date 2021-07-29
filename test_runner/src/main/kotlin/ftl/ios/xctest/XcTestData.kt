package ftl.ios.xctest

import com.dd.plist.NSDictionary
import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.internal.APP_ID
import ftl.args.ArgsHelper.calculateShards
import ftl.args.IosArgs
import ftl.args.isXcTest
import ftl.ios.xctest.common.XcTestRunVersion
import ftl.ios.xctest.common.XcTestRunVersion.V1
import ftl.ios.xctest.common.XcTestRunVersion.V2
import ftl.ios.xctest.common.XctestrunMethods
import ftl.ios.xctest.common.getBundleId
import ftl.ios.xctest.common.getXcTestRunVersion
import ftl.ios.xctest.common.mapToRegex
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.run.common.fromJson
import ftl.shard.Chunk
import ftl.shard.TestMethod
import ftl.shard.testCases
import ftl.util.FlankTestMethod
import java.io.File
import java.nio.file.Paths

data class XcTestRunData(
    val rootDir: String,
    val nsDict: NSDictionary,
    val version: XcTestRunVersion = nsDict.getXcTestRunVersion(),
    val shardTargets: Map<String, List<XctestrunMethods>> = emptyMap(),
    val shardChunks: Map<String, List<Chunk>> = emptyMap()
)

fun IosArgs.calculateXcTestRunData(): XcTestRunData =
    if (isXcTest) calculateXcTest()
    else emptyXcTestRunData()

private fun IosArgs.calculateXcTest(): XcTestRunData {
    val xcTestRunFile = File(xctestrunFile)
    val xcTestRoot: String = xcTestRunFile.parent + "/"
    val xcTestNsDictionary: NSDictionary = parseToNSDictionary(xcTestRunFile)

    val calculatedShards: Map<String, Pair<List<Chunk>, List<XctestrunMethods>>> = when {
        disableSharding -> emptyMap()
        useCustomShardingV1(xcTestNsDictionary) -> shardsFromV1()
        useCustomShardingV2(xcTestNsDictionary) -> shardsFromV2()
        else -> calculateConfigurationShards(
            xcTestRoot = xcTestRoot,
            xcTestNsDictionary = xcTestNsDictionary,
            regexList = testTargets.mapToRegex()
        )
    }

    reportBundleId()

    return XcTestRunData(
        rootDir = xcTestRoot,
        nsDict = xcTestNsDictionary,
        shardChunks = calculatedShards.mapValues { it.value.first },
        shardTargets = calculatedShards.mapValues { it.value.second },
    )
}

private fun IosArgs.reportBundleId() = Mixpanel.add(APP_ID, getBundleId())

private inline fun <reified T> createCustomSharding(shardingJsonPath: String) =
    fromJson<T>(Paths.get(shardingJsonPath).toFile().readText())

private fun IosArgs.useCustomShardingV1(dictionary: NSDictionary) =
    customShardingJson.isNotBlank() && dictionary.getXcTestRunVersion() == V1

private fun IosArgs.useCustomShardingV2(dictionary: NSDictionary) =
    customShardingJson.isNotBlank() && dictionary.getXcTestRunVersion() == V2

private fun IosArgs.shardsFromV1() = mapOf(
    "" to createCustomSharding<List<XctestrunMethods>>(commonArgs.customShardingJson)
        .run {
            map { xcMethods ->
                Chunk(xcMethods.values.flatMap { it.map(::TestMethod) })
            } to this
        }
)

private fun IosArgs.shardsFromV2() =
    createCustomSharding<Map<String, List<XctestrunMethods>>>(commonArgs.customShardingJson)
        .mapValues { (_, xcMethodsList) ->
            xcMethodsList.map { xcMethods ->
                Chunk(xcMethods.values.flatMap { it.map(::TestMethod) })
            } to xcMethodsList
        }

private fun emptyXcTestRunData() = XcTestRunData(
    rootDir = "",
    nsDict = NSDictionary(),
    version = V1
)

private fun IosArgs.filterTestConfigurationsIfNeeded(
    configurations: Map<String, Map<String, List<String>>>
): Map<String, Map<String, List<String>>> = when {
    onlyTestConfiguration.isNotBlank() -> configurations.filterKeys { it == onlyTestConfiguration }
    skipTestConfiguration.isNotBlank() -> configurations.filterKeys { it != skipTestConfiguration }
    else -> configurations
}

private fun IosArgs.calculateConfigurationShards(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary,
    regexList: List<Regex>
): Map<String, Pair<List<Chunk>, List<XctestrunMethods>>> =
    filterTestConfigurationsIfNeeded(
        findXcTestNames(
            xcTestRoot = xcTestRoot,
            xcTestNsDictionary = xcTestNsDictionary
        )
    ).mapValues { (_, targets: Map<String, List<String>>) ->
        calculateConfigurationShards(targets, regexList)
    }

private fun findXcTestNames(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary
): Map<String, Map<String, List<String>>> =
    when (xcTestNsDictionary.getXcTestRunVersion()) {
        V1 -> mapOf("" to findXcTestNamesV1(xcTestRoot, xcTestNsDictionary))
        V2 -> findXcTestNamesV2(xcTestRoot, xcTestNsDictionary)
    }

private fun IosArgs.calculateConfigurationShards(
    targets: Map<String, List<String>>,
    regexList: List<Regex>
): Pair<List<Chunk>, List<XctestrunMethods>> {
    val shardChunks = calculateShards(
        filteredTests = targets
            .values.flatten()
            .filterAnyMatches(regexList)
            .map(::FlankTestMethod),
        args = this
    ).shardChunks

    val shardTargets = shardChunks.testCases.map { shardMethods ->
        targets.mapValues { (_, methods) ->
            methods.intersect(shardMethods).toList()
        }.filterValues {
            it.isNotEmpty()
        }
    }

    return shardChunks to shardTargets
}

private fun List<String>.filterAnyMatches(
    list: List<Regex>
): List<String> =
    if (list.isEmpty()) this
    else filter { test -> list.any { regex -> regex.matches(test) } }

fun XcTestRunData.flattenShardChunks(): List<Chunk> =
    shardChunks.values.flatten()
