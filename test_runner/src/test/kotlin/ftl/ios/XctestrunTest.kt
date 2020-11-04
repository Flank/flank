package ftl.ios

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants.isWindows
import ftl.ios.xctest.common.findTestsForTestTarget
import ftl.ios.xctest.findXcTestNamesV1
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.ios.xctest.rewriteXcTestRunV1
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.normalizeLineEnding
import ftl.run.exception.FlankGeneralError
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@RunWith(FlankTestRunner::class)
class XctestrunTest {

    private val swiftXctestrun = "$FIXTURES_PATH/ios/earl_grey_example/EarlGreyExampleSwiftTests.xctestrun"
    private val multipleTargetsSwiftXctestrun = "$FIXTURES_PATH/ios/flank_ios_example/FlankExampleTests.xctestrun"

    private val swiftTests = listOf(
        "EarlGreyExampleSwiftTests/testBasicSelection",
        "EarlGreyExampleSwiftTests/testBasicSelectionActionAssert",
        "EarlGreyExampleSwiftTests/testBasicSelectionAndAction",
        "EarlGreyExampleSwiftTests/testBasicSelectionAndAssert",
        "EarlGreyExampleSwiftTests/testCatchErrorOnFailure",
        "EarlGreyExampleSwiftTests/testCollectionMatchers",
        "EarlGreyExampleSwiftTests/testCustomAction",
        "EarlGreyExampleSwiftTests/testLayout",
        "EarlGreyExampleSwiftTests/testSelectionOnMultipleElements",
        "EarlGreyExampleSwiftTests/testTableCellOutOfScreen",
        "EarlGreyExampleSwiftTests/testThatThrows",
        "EarlGreyExampleSwiftTests/testWithCondition",
        "EarlGreyExampleSwiftTests/testWithCustomAssertion",
        "EarlGreyExampleSwiftTests/testWithCustomFailureHandler",
        "EarlGreyExampleSwiftTests/testWithCustomMatcher",
        "EarlGreyExampleSwiftTests/testWithGreyAssertions",
        "EarlGreyExampleSwiftTests/testWithInRoot"
    )

    @Test
    fun parse() {
        assumeFalse(isWindows)
        val result = parseToNSDictionary(swiftXctestrun)
        assertThat(arrayOf("EarlGreyExampleSwiftTests", "__xctestrun_metadata__")).isEqualTo(result.allKeys())
        val dict = result["EarlGreyExampleSwiftTests"] as NSDictionary
        assertThat(dict.count()).isEqualTo(20)
        assertThat(dict.containsKey("OnlyTestIdentifiers")).isFalse()
    }

    @Test(expected = FlankGeneralError::class)
    fun `parse fileNotFound`() {
        parseToNSDictionary("./XctestrunThatDoesNotExist")
    }

    @Test
    fun findTestNames() {
        assumeFalse(isWindows)

        val names = findXcTestNamesV1(swiftXctestrun)
            .flatMap { it.value }
            .sorted()
        assertThat(swiftTests).isEqualTo(names)
    }

    @Test
    fun rewrite() {
        assumeFalse(isWindows)

        val root = parseToNSDictionary(swiftXctestrun)
        val methods = findTestsForTestTarget(testTarget = "EarlGreyExampleSwiftTests", xctestrun = File(swiftXctestrun))
        val methodsData = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to methods)

        val results = String(rewriteXcTestRunV1(root, methodsData))

        assertThat(results.contains("OnlyTestIdentifiers")).isTrue()
    }

    @Test
    fun rewriteImmutable() {
        assumeFalse(isWindows)

        val root = parseToNSDictionary(swiftXctestrun)
        val methods = findTestsForTestTarget(testTarget = "EarlGreyExampleSwiftTests", xctestrun = File(swiftXctestrun))
        val methodsData = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to methods)

        // ensure root object isn't modified. Rewrite should return a new object.
        val key = "OnlyTestIdentifiers"

        assertThat(root.toASCIIPropertyList().contains(key)).isFalse()

        rewriteXcTestRunV1(root, methodsData)

        assertThat(root.toASCIIPropertyList().contains(key)).isFalse()
    }

    @Test
    fun `rewrite idempotent`() {
        val root = NSDictionary()
        root["EarlGreyExampleSwiftTests"] = NSDictionary()
        root["EarlGreyExampleTests"] = NSDictionary()
        val methods = listOf("testOne", "testTwo")
        val methodsData = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to methods, "EarlGreyExampleTests" to methods)
        rewriteXcTestRunV1(root, methodsData)
        rewriteXcTestRunV1(root, methodsData)
        val result = rewriteXcTestRunV1(root, methodsData)
        val expected = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>EarlGreyExampleSwiftTests</key>
	<dict>
		<key>OnlyTestIdentifiers</key>
		<array>
			<string>testOne</string>
			<string>testTwo</string>
		</array>
	</dict>
	<key>EarlGreyExampleTests</key>
	<dict>
		<key>OnlyTestIdentifiers</key>
		<array>
			<string>testOne</string>
			<string>testTwo</string>
		</array>
	</dict>
</dict>
</plist>"""
        assertThat(expected).isEqualTo(String(result).normalizeLineEnding())
    }

    @Test
    fun `rewrite preserves skip`() {
        val inputXml = """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>EarlGreyExampleSwiftTests</key>
	<dict>
		<key>SkipTestIdentifiers</key>
		<array>
			<string>testTwo</string>
		</array>
		<key>OnlyTestIdentifiers</key>
		<array>
			<string>testOne</string>
			<string>testTwo</string>
		</array>
	</dict>
</dict>
</plist>
        """.trimIndent()
        val root = parseToNSDictionary(inputXml.toByteArray())

        val tests = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to listOf("testOne", "testTwo"))
        val rewrittenXml = String(rewriteXcTestRunV1(root, tests))

        assertThat(inputXml).isEqualTo(rewrittenXml.normalizeLineEnding())
    }

    @Test
    fun `findTestNames respects skip`() {
        assumeFalse(isWindows)

        val inputXml = """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>EarlGreyExampleSwiftTests</key>
	<dict>
        <key>DependentProductPaths</key>
		<array>
			<string>__TESTROOT__/Debug-iphoneos/EarlGreyExampleSwift.app/PlugIns/EarlGreyExampleSwiftTests.xctest</string>
			<string>__TESTROOT__/Debug-iphoneos/EarlGreyExampleSwift.app</string>
		</array>
		<key>SkipTestIdentifiers</key>
		<array>
            <string>EarlGreyExampleSwiftTests/testBasicSelectionActionAssert</string>
            <string>EarlGreyExampleSwiftTests/testBasicSelectionAndAction</string>
            <string>EarlGreyExampleSwiftTests/testBasicSelectionAndAssert</string>
            <string>EarlGreyExampleSwiftTests/testCatchErrorOnFailure</string>
            <string>EarlGreyExampleSwiftTests/testCollectionMatchers</string>
            <string>EarlGreyExampleSwiftTests/testCustomAction</string>
            <string>EarlGreyExampleSwiftTests/testLayout</string>
            <string>EarlGreyExampleSwiftTests/testSelectionOnMultipleElements</string>
            <string>EarlGreyExampleSwiftTests/testTableCellOutOfScreen</string>
            <string>EarlGreyExampleSwiftTests/testThatThrows</string>
            <string>EarlGreyExampleSwiftTests/testWithCondition</string>
            <string>EarlGreyExampleSwiftTests/testWithCustomAssertion</string>
            <string>EarlGreyExampleSwiftTests/testWithCustomFailureHandler</string>
            <string>EarlGreyExampleSwiftTests/testWithCustomMatcher</string>
            <string>EarlGreyExampleSwiftTests/testWithGreyAssertions</string>
            <string>EarlGreyExampleSwiftTests/testWithInRoot</string>
		</array>
	</dict>
</dict>
</plist>
        """.trimIndent()

        val tmpXml = Paths.get(FIXTURES_PATH, "skip.xctestrun")
        Files.write(tmpXml, inputXml.toByteArray())
        tmpXml.toFile().deleteOnExit()

        val actualTests = findTestsForTestTarget("EarlGreyExampleSwiftTests", tmpXml.toFile()).sorted()
        assertThat(actualTests).isEqualTo(listOf("EarlGreyExampleSwiftTests/testBasicSelection"))
    }

    @Test
    fun findTestNamesForTestTarget() {
        assumeFalse(isWindows)
        val names = findTestsForTestTarget(testTarget = "EarlGreyExampleSwiftTests", xctestrun = File(swiftXctestrun)).sorted()
        assertThat(swiftTests).isEqualTo(names)
    }

    @Test(expected = FlankGeneralError::class)
    fun `findTestNames for nonexisting test target`() {
        assumeFalse(isWindows)
        findTestsForTestTarget(testTarget = "Incorrect", xctestrun = File(swiftXctestrun)).sorted()
    }

    @Test
    fun `find test names for xctestrun file containing multiple test targets`() {
        assumeFalse(isWindows)
        val names = findTestsForTestTarget(testTarget = "FlankExampleTests", xctestrun = File(multipleTargetsSwiftXctestrun)).sorted()
        assertThat(names).isEqualTo(listOf("FlankExampleTests/test1", "FlankExampleTests/test2"))

        val names2 = findTestsForTestTarget(testTarget = "FlankExampleSecondTests", xctestrun = File(multipleTargetsSwiftXctestrun)).sorted()
        assertThat(names2).isEqualTo(listOf("FlankExampleSecondTests/test3", "FlankExampleSecondTests/test4"))
    }

    @Test
    fun `rewrite methods in single test target`() {
        assumeFalse(isWindows)
        val methods = listOf("EarlGreyExampleSwiftTests/testBasicSelectionActionAssert", "EarlGreyExampleSwiftTests/testBasicSelection")
        val result = rewriteXcTestRunV1(xctestrun = swiftXctestrun, methods)
        val resultXML = parseToNSDictionary(result)
        val testTarget = resultXML["EarlGreyExampleSwiftTests"] as NSDictionary
        val resultMethods = testTarget["OnlyTestIdentifiers"] as NSArray

        assertThat(methods.toSet()).isEqualTo(resultMethods.array.map { it.toJavaObject() }.toSet())
    }

    @Test
    fun `rewrite methods in multiple test targets`() {
        assumeFalse(isWindows) // TODO enable it on #1180

        val expectedMethods1 = listOf("FlankExampleTests/test1", "FlankExampleTests/test2")
        val expectedMethods2 = listOf("FlankExampleSecondTests/test3")

        val result = rewriteXcTestRunV1(xctestrun = multipleTargetsSwiftXctestrun, listOf(expectedMethods1, expectedMethods2).flatMap { it })
        val resultXML = parseToNSDictionary(result)

        val target1 = resultXML["FlankExampleTests"] as NSDictionary
        val target2 = resultXML["FlankExampleSecondTests"] as NSDictionary
        val resultMethods1 = target1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = target2["OnlyTestIdentifiers"] as NSArray

        assertThat(expectedMethods1.toSet()).isEqualTo(resultMethods1.array.map { it.toJavaObject() }.toSet())
        assertThat(expectedMethods2.toSet()).isEqualTo(resultMethods2.array.map { it.toJavaObject() }.toSet())
    }

    @Test
    fun `rewrite incorrect methods in multiple test targets`() {
        assumeFalse(isWindows) // TODO enable it on #1180

        val methods1 = listOf("incorrect1", "incorrect2")
        val methods2 = listOf("incorrect3")

        val result = rewriteXcTestRunV1(xctestrun = multipleTargetsSwiftXctestrun, listOf(methods1, methods2).flatMap { it })
        val resultXML = parseToNSDictionary(result)

        val target1 = resultXML["FlankExampleTests"] as NSDictionary
        val target2 = resultXML["FlankExampleSecondTests"] as NSDictionary
        val resultMethods1 = target1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = target2["OnlyTestIdentifiers"] as NSArray

        assertThat(resultMethods1.array.isEmpty()).isTrue()
        assertThat(resultMethods2.array.isEmpty()).isTrue()
    }

    @Test
    fun `rewrite mix of correct and incorrect methods in multiple test targets`() {
        assumeFalse(isWindows) // TODO enable it on #1180

        val methods1 = listOf("FlankExampleTests/test1", "FlankExampleTests/test2", "incorrect1")
        val methods2 = listOf("FlankExampleSecondTests/test3", "incorrect2")

        val expectedMethods1 = listOf("FlankExampleTests/test1", "FlankExampleTests/test2")
        val expectedMethods2 = listOf("FlankExampleSecondTests/test3")

        val result = rewriteXcTestRunV1(xctestrun = multipleTargetsSwiftXctestrun, listOf(methods1, methods2).flatMap { it })
        val resultXML = parseToNSDictionary(result)

        val target1 = resultXML["FlankExampleTests"] as NSDictionary
        val target2 = resultXML["FlankExampleSecondTests"] as NSDictionary
        val resultMethods1 = target1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = target2["OnlyTestIdentifiers"] as NSArray

        assertThat(expectedMethods1.toSet()).isEqualTo(resultMethods1.array.map { it.toJavaObject() }.toSet())
        assertThat(expectedMethods2.toSet()).isEqualTo(resultMethods2.array.map { it.toJavaObject() }.toSet())
    }
}
