package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.ios.xctest.common.findTestsForTarget
import ftl.ios.xctest.common.isMetadata
import ftl.ios.xctest.common.parseToNSDictionary
import java.io.File

internal fun findXcTestNamesV1(
    xctestrun: String
): Map<String, List<String>> =
    findXcTestNamesV1(File(xctestrun))

private fun findXcTestNamesV1(
    xctestrun: File
): Map<String, List<String>> =
    findXcTestNamesV1(
        xcTestRoot = xctestrun.parent + "/",
        xcTestNsDictionary = parseToNSDictionary(xctestrun)
    )

internal fun findXcTestNamesV1(
    xcTestRoot: String,
    xcTestNsDictionary: NSDictionary
): Map<String, List<String>> =
    xcTestNsDictionary
        .allKeys()
        .filterNot(String::isMetadata)
        .map { testTarget ->
            testTarget to findTestsForTarget(
                testRoot = xcTestRoot,
                testTargetDict = xcTestNsDictionary[testTarget] as NSDictionary,
                testTargetName = testTarget,
            )
        }
        .distinct()
        .toMap()
