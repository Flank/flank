package ftl.ios.xctest

import ftl.ios.xctest.common.XctestrunMethods
import ftl.ios.xctest.common.findTestsForTarget
import ftl.ios.xctest.common.getBlueprintName
import ftl.ios.xctest.common.getTestConfigurations
import ftl.ios.xctest.common.getTestTargets
import ftl.ios.xctest.common.parseToNSDictionary
import java.io.File

internal fun findXcTestNamesV2(xctestrun: String): Map<String, XctestrunMethods> =
    findXcTestNamesV2(File(xctestrun))

private fun findXcTestNamesV2(xctestrun: File): Map<String, XctestrunMethods> {
    val testRoot = xctestrun.parent + "/"
    return parseToNSDictionary(xctestrun).getTestConfigurations().mapValues { (_, configDict) ->
        configDict.getTestTargets()
            .associateBy { targetDict -> targetDict.getBlueprintName() }
            .mapValues { (name, dict) ->
                findTestsForTarget(
                    testRoot = testRoot,
                    testTargetName = name,
                    testTargetDict = dict,
                )
            }
    }
}
