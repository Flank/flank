package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSObject
import com.dd.plist.NSString
import com.google.common.annotations.VisibleForTesting
import ftl.run.exception.FlankGeneralError
import java.io.File
import java.nio.file.Paths

internal fun findXcTestNamesV1(xctestrun: String): XctestrunMethods =
    findXcTestNamesV1(File(xctestrun))

private fun findXcTestNamesV1(xctestrun: File): XctestrunMethods =
    parseToNSDictionary(xctestrun).run {
        val testRoot = xctestrun.parent + "/"
        allKeys().filterNot(String::isMetadata).map { testTarget ->
            testTarget to findTestsForTarget(
                testRoot = testRoot,
                testTargetDict = get(testTarget) as NSDictionary,
                testTargetKey = testTarget,
            )
        }.distinct().toMap()
    }

internal fun findXcTestNamesV2(xctestrun: String): XctestrunMethods =
    findXcTestNamesV2(File(xctestrun))

private fun findXcTestNamesV2(xctestrun: File): XctestrunMethods =
    parseToNSDictionary(xctestrun).run {
        allKeys().map { testTarget ->
            testTarget to findTestsForTarget(
                testRoot = xctestrun.parent + "/",
                testTargetDict = get(testTarget) as NSDictionary,
                testTargetKey = testTarget,
            )
        }.distinct().toMap()
    }

private fun findTestsForTarget(
    testRoot: String,
    testTargetDict: NSDictionary,
    testTargetKey: String,
): List<String> = testTargetDict
    .findXcTestTargets(testTargetKey)
    .findBinaryTests(testRoot) - testTargetDict
    .findTestsToSkip()

fun NSDictionary.findXcTestTargets(testTarget: String): NSObject =
    get("DependentProductPaths")
        ?.let { it as? NSArray }?.array
        ?.first { product -> product.toString().contains("/$testTarget.xctest") }
        ?: throw FlankGeneralError("Test target $testTarget doesn't exist")

fun NSObject.findBinaryTests(testRoot: String): List<String> {
    val binaryRoot = toString().replace("__TESTROOT__/", testRoot)
    println("Found xctest: $binaryRoot")

    val binaryName = File(binaryRoot).nameWithoutExtension
    val binaryPath = Paths.get(binaryRoot, binaryName).toString()

    return (parseObjcTests(binaryPath) + parseSwiftTests(binaryPath)).distinct()
}

private fun NSDictionary.findTestsToSkip(): List<String> =
    get("SkipTestIdentifiers")
        ?.let { it as? NSArray }?.array
        ?.mapNotNull { (it as? NSString)?.content }
        ?: emptyList()

@VisibleForTesting // TODO Remove it, cause is used only in tests
internal fun findTestsForTestTarget(testTarget: String, xctestrun: File): List<String> =
    parseToNSDictionary(xctestrun)[testTarget]
        ?.let { it as? NSDictionary }
        ?.let { findTestsForTarget(xctestrun.parent + "/", it, testTarget) }
        ?: throw FlankGeneralError("Test target $testTarget doesn't exist")
