package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.ios.xctest.common.XctestrunMethods
import ftl.ios.xctest.common.findTestsForTarget
import ftl.ios.xctest.common.isMetadata
import ftl.ios.xctest.common.parseToNSDictionary
import java.io.File

internal fun findXcTestNamesV1(xctestrun: String): XctestrunMethods =
    findXcTestNamesV1(File(xctestrun))

private fun findXcTestNamesV1(xctestrun: File): XctestrunMethods =
    parseToNSDictionary(xctestrun).run {
        val testRoot = xctestrun.parent + "/"
        allKeys().filterNot(String::isMetadata).map { testTarget ->
            testTarget to findTestsForTarget(
                testRoot = testRoot,
                testTargetDict = get(testTarget) as NSDictionary,
                testTargetName = testTarget,
            )
        }.distinct().toMap()
    }
