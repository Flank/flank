package xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import java.io.File
import java.nio.file.Paths

object Shard {

    private fun testsForTarget(testDictionary: NSDictionary, testTarget: String, testRoot: String): Set<String> {
        val productPaths = (testDictionary["DependentProductPaths"] as NSArray)
        for (product in productPaths.array) {
            val productString = product.toString()
            if (productString.contains("/$testTarget.xctest")) {
                val binaryRoot = productString.replace("__TESTROOT__/", testRoot)
                println("Found product: $binaryRoot")

                val isSwift = Paths.get(binaryRoot, "libswiftRemoteMirror.dylib").toFile().exists()
                val binaryName = File(binaryRoot).nameWithoutExtension
                val binaryPath = Paths.get(binaryRoot, binaryName).toString()

                return if (isSwift) {
                    println("Swift detected")
                    Parse.parseObjcTests(binaryPath) + Parse.parseSwiftTests(binaryPath)
                } else {
                    println("Objc detected")
                    Parse.parseObjcTests(binaryPath)
                }
            }
        }

        throw RuntimeException("No tests found")
    }

    // https://github.com/google/xctestrunner/blob/51dbb6b7eb35f2ed55439459ca49e06992bc4da0/xctestrunner/test_runner/xctestrun.py#L129
    private const val onlyTestIdentifiers = "OnlyTestIdentifiers"

    private fun setOnlyTestIdentifiers(testDictionary: NSDictionary, tests: List<String>) {
        val nsArray = NSArray(tests.size)
        tests.forEachIndexed { index, test ->  nsArray.setValue(index, test) }

        testDictionary.remove(onlyTestIdentifiers)
        testDictionary["OnlyTestIdentifiers"] = nsArray
    }

    private fun update(xctestrunPath: String) {
        val xctestrun = File(xctestrunPath).canonicalFile
        if (!xctestrun.exists()) throw RuntimeException("$xctestrun doesn't exist")

        // EarlGreyExampleSwiftTests_iphoneos11.3-arm64.xctestrun => EarlGreyExampleSwiftTests
        val testRoot = xctestrun.parent + "/"

        val root = PropertyListParser.parse(xctestrun) as NSDictionary

        // OnlyTestIdentifiers
        // Test-Class-Name[/Test-Method-Name]
        // Example/testExample
        for (testTarget in root.allKeys()) {
            val testDictionary = (root[testTarget] as NSDictionary)

            val methods = testsForTarget(testDictionary = testDictionary,
                    testRoot = testRoot,
                    testTarget = testTarget)
            println("Found ${methods.size}x tests")

            // run one method for now
            setOnlyTestIdentifiers(testDictionary, listOf(methods.first()))
        }

        PropertyListParser.saveAsXML(root, xctestrun)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // TODO: remove
        val testRunPath = "./src/test/kotlin/xctest/fixtures/EarlGreyExampleSwiftTests/EarlGreyExampleSwiftTests_iphoneos11.3-arm64.xctestrun"

        update(testRunPath)
    }
}
