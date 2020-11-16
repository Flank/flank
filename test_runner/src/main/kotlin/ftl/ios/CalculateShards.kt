package ftl.ios

import com.dd.plist.NSDictionary
import ftl.args.ArgsHelper.calculateShards
import ftl.args.IosArgs
import ftl.ios.xctest.common.getXcTestRunVersion
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.ios.xctest.findXcTestNamesV1
import ftl.ios.xctest.findXcTestNamesV2
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import ftl.shard.Chunk
import ftl.util.FlankTestMethod
import java.io.File

fun IosArgs.calculateXcTestRunData(): XcTestRunData {
    val iosArgs = this
    val xcTestRunFile = File(xctestrunFile)
    val xcTestRoot: String = xcTestRunFile.parent + "/"
    val xcTestNsDictionary: NSDictionary = parseToNSDictionary(xcTestRunFile)
    val xcTestVersion: Int = xcTestNsDictionary.getXcTestRunVersion()
    val regexList = testTargets.mapToRegex()

    val configurations: Map<String, Map<String, List<String>>> =
        when (xcTestVersion) {
            1 -> mapOf("" to findXcTestNamesV1(xcTestRoot, xcTestNsDictionary))
            2 -> findXcTestNamesV2(xcTestRoot, xcTestNsDictionary)
            else -> throw FlankGeneralError("Unsupported xctestrun version $xcTestVersion")
        }

    val filteredMethods: Map<String, Map<String, List<FlankTestMethod>>> =
        configurations.mapValues { (_, targets) ->
            targets.mapValues { (_, methods) ->
                methods
                    .filterAnyMatches(regexList)
                    .map(::FlankTestMethod)
            }
        }

    val calculatedShards: Map<String, Map<String, List<Chunk>>> =
        filteredMethods.mapValues { (_, targets) ->
            targets.mapValues { (_, methods) ->
                calculateShards(methods, iosArgs).shardChunks
            }
        }

    return XcTestRunData(
        version = xcTestVersion,
        rootDir = xcTestRoot,
        nsDict = xcTestNsDictionary,
        shards = calculatedShards
    )
}

data class XcTestRunData(
    val version: Int,
    val rootDir: String,
    val nsDict: NSDictionary,
    val shards: Map<String, Map<String, List<Chunk>>>
)

private fun List<String>.mapToRegex(): List<Regex> = map { filter ->
    try {
        filter.toRegex()
    } catch (e: Exception) {
        throw FlankConfigurationError("Invalid regex: $filter", e)
    }
}

internal fun List<String>.filterAnyMatches(
    list: List<Regex>
): List<String> =
    if (list.isEmpty()) this
    else filter { test -> list.any { regex -> regex.matches(test) } }
