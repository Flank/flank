package ftl.ios.xctest.common

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSObject
import com.dd.plist.NSString
import com.google.common.annotations.VisibleForTesting
import flank.common.logLn
import ftl.run.exception.FlankGeneralError
import java.io.File
import java.nio.file.Paths

internal fun findTestsForTarget(
    testRoot: String,
    testTargetDict: NSDictionary,
    testTargetName: String,
    globalTestInclusion: Boolean,
): List<String> = testTargetDict
    .findXcTestTargets(testTargetName)
    .findBinaryTests(testRoot, globalTestInclusion) - testTargetDict
    .findTestsToSkip()

private fun NSDictionary.findXcTestTargets(
    testTarget: String
): NSObject =
    get("DependentProductPaths")
        ?.let { it as? NSArray }?.array
        ?.first { product -> product.toString().contains("/$testTarget.xctest") }
        ?: throw FlankGeneralError("Test target $testTarget doesn't exist")

private fun NSObject.findBinaryTests(testRoot: String, globalTestInclusion: Boolean): List<String> {
    val binaryRoot = toString().replace("__TESTROOT__/", testRoot)
    logLn("Found xctest: $binaryRoot")

    val binaryName = File(binaryRoot).nameWithoutExtension
    val binaryPath = Paths.get(binaryRoot, binaryName).toString()

    return (parseObjcTests(binaryPath) + parseSwiftTests(binaryPath, globalTestInclusion)).distinct()
}

private fun NSDictionary.findTestsToSkip(): List<String> =
    get("SkipTestIdentifiers")
        ?.let { it as? NSArray }?.array
        ?.mapNotNull { (it as? NSString)?.content }
        ?: emptyList()

@VisibleForTesting // TODO Remove it, cause is used only in tests
internal fun findTestsForTestTarget(testTarget: String, xctestrun: File, globalTestInclusion: Boolean): List<String> =
    parseToNSDictionary(xctestrun)[testTarget]
        ?.let { it as? NSDictionary }
        ?.let { findTestsForTarget(xctestrun.parent + "/", it, testTarget, globalTestInclusion) }
        ?: throw FlankGeneralError("Test target $testTarget doesn't exist")
