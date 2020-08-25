package ftl.ios

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSString
import com.dd.plist.PropertyListParser
import ftl.run.exception.FlankGeneralError
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths

object Xctestrun {

    private fun String.isMetadata(): Boolean {
        return this.contentEquals("__xctestrun_metadata__")
    }

    // Parses all tests for a given target
    private fun testsForTarget(testDictionary: NSDictionary, testTarget: String, testRoot: String): List<String> {
        if (testTarget.isMetadata()) return emptyList()
        val skipTestIdentifiers: NSArray? = testDictionary["SkipTestIdentifiers"] as NSArray?
        val skipTests: List<String> = skipTestIdentifiers?.array?.mapNotNull { (it as NSString?)?.content } ?: listOf()
        val productPaths = testDictionary["DependentProductPaths"] as NSArray
        for (product in productPaths.array) {
            val productString = product.toString()
            if (productString.contains("/$testTarget.xctest")) {
                val binaryRoot = productString.replace("__TESTROOT__/", testRoot)
                println("Found xctest: $binaryRoot")

                val binaryName = File(binaryRoot).nameWithoutExtension
                val binaryPath = Paths.get(binaryRoot, binaryName).toString()

                val tests = (Parse.parseObjcTests(binaryPath) + Parse.parseSwiftTests(binaryPath)).distinct()

                return tests.minus(skipTests)
            }
        }

        throw FlankGeneralError("No tests found")
    }

    // https://github.com/google/xctestrunner/blob/51dbb6b7eb35f2ed55439459ca49e06992bc4da0/xctestrunner/test_runner/xctestrun.py#L129
    private const val onlyTestIdentifiers = "OnlyTestIdentifiers"

    // Rewrites tests so that only the listed tests execute
    private fun setOnlyTestIdentifiers(testDictionary: NSDictionary, tests: Collection<String>) {
        val nsArray = NSArray(tests.size)
        tests.forEachIndexed { index, test -> nsArray.setValue(index, test) }

        while (testDictionary.containsKey(onlyTestIdentifiers)) {
            testDictionary.remove(onlyTestIdentifiers)
        }

        testDictionary[onlyTestIdentifiers] = nsArray
    }

    fun parse(xctestrun: String): NSDictionary {
        return parse(File(xctestrun))
    }

    // Parses xctestrun file into a dictonary
    fun parse(xctestrun: File): NSDictionary {
        val testrun = xctestrun.canonicalFile
        if (!testrun.exists()) throw FlankGeneralError("$testrun doesn't exist")

        return PropertyListParser.parse(testrun) as NSDictionary
    }

    fun parse(xctestrun: ByteArray): NSDictionary {
        return PropertyListParser.parse(xctestrun) as NSDictionary
    }

    fun findTestNames(xctestrun: String): List<String> {
        return findTestNames(File(xctestrun))
    }

    // Finds tests in a xctestrun file
    private fun findTestNames(xctestrun: File): List<String> {
        val root = parse(xctestrun)
        val result = mutableListOf<String>()
        // EarlGreyExampleSwiftTests_iphoneos11.3-arm64.xctestrun => EarlGreyExampleSwiftTests
        val testRoot = xctestrun.parent + "/"

        // OnlyTestIdentifiers
        // Test-Class-Name[/Test-Method-Name]
        // Example/testExample
        for (testTarget in root.allKeys()) {
            val testDictionary = (root[testTarget] as NSDictionary)

            val methods = testsForTarget(
                testDictionary = testDictionary,
                testRoot = testRoot,
                testTarget = testTarget
            )

            result += methods
        }

        return result.distinct()
    }

    fun rewrite(root: NSDictionary, methods: Collection<String>): ByteArray {
        val rootClone = root.clone()
        for (testTarget in rootClone.allKeys()) {
            if (testTarget.isMetadata()) continue
            val testDictionary = (rootClone[testTarget] as NSDictionary)
            setOnlyTestIdentifiers(testDictionary, methods)
        }

        return rootClone.toByteArray()
    }

    fun NSDictionary.toByteArray(): ByteArray {
        val out = ByteArrayOutputStream()
        PropertyListParser.saveAsXML(this, out)
        return out.toByteArray()
    }
}
