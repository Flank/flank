package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.ios.xctest.common.findTestsForTarget
import ftl.ios.xctest.common.getBlueprintName
import ftl.ios.xctest.common.getTestConfigurations
import ftl.ios.xctest.common.getTestTargets

internal fun findXcTestNamesV2(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary,
    globalTestInclusion: Boolean
): Map<String, Map<String, List<String>>> =
    xcTestNsDictionary
        .getTestConfigurations()
        .mapValues { (_, configDict: NSDictionary) ->
            configDict.findTestTargetMethods(xcTestRoot, globalTestInclusion)
        }

private fun NSDictionary.findTestTargetMethods(
    xcTestRoot: String,
    globalTestInclusion: Boolean
): Map<String, List<String>> =
    getTestTargets()
        .associateBy { targetDict -> targetDict.getBlueprintName() }
        .mapValues { (name, dict) ->
            findTestsForTarget(
                testRoot = xcTestRoot,
                testTargetName = name,
                testTargetDict = dict,
                globalTestInclusion = globalTestInclusion
            )
        }
