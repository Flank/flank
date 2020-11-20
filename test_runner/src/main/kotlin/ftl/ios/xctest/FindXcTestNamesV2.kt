package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.ios.xctest.common.findTestsForTarget
import ftl.ios.xctest.common.getBlueprintName
import ftl.ios.xctest.common.getTestConfigurations
import ftl.ios.xctest.common.getTestTargets

internal fun findXcTestNamesV2(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary
): Map<String, Map<String, List<String>>> =
    xcTestNsDictionary
        .getTestConfigurations()
        .mapValues { (_, configDict) ->
            configDict
                .getTestTargets()
                .associateBy { targetDict -> targetDict.getBlueprintName() }
                .mapValues { (name, dict) ->
                    findTestsForTarget(
                        testRoot = xcTestRoot,
                        testTargetName = name,
                        testTargetDict = dict,
                    )
                }
        }
