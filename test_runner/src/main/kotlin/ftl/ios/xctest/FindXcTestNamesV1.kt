package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.ios.xctest.common.findTestsForTarget
import ftl.ios.xctest.common.isMetadata

internal fun findXcTestNamesV1(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary,
    globalTestInclusion: Boolean = true,
): Map<String, List<String>> =
    xcTestNsDictionary
        .allKeys()
        .filterNot(String::isMetadata)
        .map { testTarget ->
            testTarget to findTestsForTarget(
                testRoot = xcTestRoot,
                testTargetDict = xcTestNsDictionary[testTarget] as NSDictionary,
                testTargetName = testTarget,
                globalTestInclusion = globalTestInclusion
            )
        }
        .distinct()
        .toMap()
