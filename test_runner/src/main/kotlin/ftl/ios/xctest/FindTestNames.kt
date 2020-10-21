package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSObject
import com.dd.plist.NSString
import ftl.ios.Parse
import ftl.run.exception.FlankGeneralError
import java.io.File
import java.nio.file.Paths

// Finds tests in a xctestrun file
internal fun findTestNames(xctestrun: File): List<String> =
    parseToNSDictionary(xctestrun).run {
        val testRoot = xctestrun.parent + "/"
        allKeys().map { testTarget ->
            (get(testTarget) as NSDictionary).findTestsForTarget(
                testRoot = testRoot,
                testTarget = testTarget
            )
        }.flatten().distinct()
    }

private fun NSDictionary.findTestsForTarget(testTarget: String, testRoot: String): List<String> =
    if (testTarget.isMetadata()) emptyList()
    else findXcTestTargets(testTarget)
        ?.run { findBinaryTests(testRoot) - findTestsToSkip() }
        ?: throw FlankGeneralError("No tests found")

private fun NSDictionary.findXcTestTargets(testTarget: String): NSObject? =
    get("DependentProductPaths")
        ?.let { it as? NSArray }?.array
        ?.first { product -> product.toString().containsTestTarget(testTarget) }

private fun String.containsTestTarget(name: String): Boolean = contains("/$name.xctest")

private fun NSObject.findBinaryTests(testRoot: String): List<String> {
    val binaryRoot = toString().replace("__TESTROOT__/", testRoot)
    println("Found xctest: $binaryRoot")

    val binaryName = File(binaryRoot).nameWithoutExtension
    val binaryPath = Paths.get(binaryRoot, binaryName).toString()

    return (Parse.parseObjcTests(binaryPath) + Parse.parseSwiftTests(binaryPath)).distinct()
}

private fun NSDictionary.findTestsToSkip(): List<String> =
    get("SkipTestIdentifiers")
        ?.let { it as? NSArray }?.array
        ?.mapNotNull { (it as? NSString)?.content }
        ?: emptyList()
