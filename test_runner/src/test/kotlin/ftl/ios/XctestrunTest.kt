package ftl.ios

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants.isWindows
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.normalizeLineEnding
import ftl.run.exception.FlankGeneralError
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.file.Files
import java.nio.file.Paths

@RunWith(FlankTestRunner::class)
class XctestrunTest {

    private val swiftXctestrun = "$FIXTURES_PATH/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun"
    private val multipleTargetsSwiftXctestrun = "$FIXTURES_PATH/axel/AllTests_iphoneos13.7-arm64e.xctestrun"

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
        val result = Xctestrun.parse(swiftXctestrun)
        assertThat(arrayOf("EarlGreyExampleSwiftTests", "__xctestrun_metadata__")).isEqualTo(result.allKeys())
        val dict = result["EarlGreyExampleSwiftTests"] as NSDictionary
        assertThat(dict.count()).isEqualTo(19)
        assertThat(dict.containsKey("OnlyTestIdentifiers")).isFalse()
    }

    @Test(expected = FlankGeneralError::class)
    fun `parse fileNotFound`() {
        Xctestrun.parse("./XctestrunThatDoesNotExist")
    }

    @Test
    fun findTestNames() {
        assumeFalse(isWindows)

        val names = Xctestrun.findTestNames(swiftXctestrun)
                .flatMap { it.value }
                .sorted()
        assertThat(swiftTests).isEqualTo(names)
    }

    @Test
    fun rewrite() {
        assumeFalse(isWindows)

        val root = Xctestrun.parse(swiftXctestrun)
        val methods = Xctestrun.findTestNames(testTarget = "EarlGreyExampleSwiftTests", xctestrun = swiftXctestrun)
        val methodsData = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to methods)

        val results = String(Xctestrun.rewrite(root, methodsData))

        assertThat(results.contains("OnlyTestIdentifiers")).isTrue()
    }

    @Test
    fun rewriteImmutable() {
        assumeFalse(isWindows)

        val root = Xctestrun.parse(swiftXctestrun)
        val methods = Xctestrun.findTestNames(testTarget = "EarlGreyExampleSwiftTests", xctestrun = swiftXctestrun)
        val methodsData = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to methods)

        // ensure root object isn't modified. Rewrite should return a new object.
        val key = "OnlyTestIdentifiers"

        assertThat(root.toASCIIPropertyList().contains(key)).isFalse()

        Xctestrun.rewrite(root, methodsData)

        assertThat(root.toASCIIPropertyList().contains(key)).isFalse()
    }

    @Test
    fun `rewrite idempotent`() {
        val root = NSDictionary()
        root["EarlGreyExampleSwiftTests"] = NSDictionary()
        root["EarlGreyExampleTests"] = NSDictionary()
        val methods = listOf("testOne", "testTwo")
        val methodsData = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to methods, "EarlGreyExampleTests" to methods)
        Xctestrun.rewrite(root, methodsData)
        Xctestrun.rewrite(root, methodsData)
        val result = Xctestrun.rewrite(root, methodsData)
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
        val root = Xctestrun.parse(inputXml.toByteArray())

        val tests = mapOf<String, List<String>>("EarlGreyExampleSwiftTests" to listOf("testOne", "testTwo"))
        val rewrittenXml = String(Xctestrun.rewrite(root, tests))

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

        val actualTests = Xctestrun.findTestNames("EarlGreyExampleSwiftTests", tmpXml.toString()).sorted()
        assertThat(actualTests).isEqualTo(listOf("EarlGreyExampleSwiftTests/testBasicSelection"))
    }

    // New
    @Test
    fun findTestNamesForTestTarget() {
        assumeFalse(isWindows)
        val names = Xctestrun.findTestNames(testTarget = "EarlGreyExampleSwiftTests", xctestrun = swiftXctestrun).sorted()
        assertThat(swiftTests).isEqualTo(names)
    }

    @Test(expected = FlankGeneralError::class)
    fun `findTestNames for nonexisting test target`() {
        assumeFalse(isWindows)
        Xctestrun.findTestNames(testTarget = "Incorrect", xctestrun = swiftXctestrun).sorted()
    }

    fun `find test names for xctestrun file containing multiple test targets`() {
        assumeFalse(isWindows)
        val names = Xctestrun.findTestNames(testTarget = "SwiftTests1", xctestrun = multipleTargetsSwiftXctestrun).sorted()
        assertThat(names).isEqualTo(listOf("SuiteA/testA1", "SuiteA/testA2", "SuiteB/testB1", "SuiteB/testB2"))

        val names2 = Xctestrun.findTestNames(testTarget = "SwiftTests2", xctestrun = multipleTargetsSwiftXctestrun).sorted()
        assertThat(names2).isEqualTo(listOf("SwiftTests2/tests2_test1", "SwiftTests2/tests2_test2"))
    }

    @Test
    fun `rewrite methods in single test target`() {
        assumeFalse(isWindows)
        val methods = listOf("EarlGreyExampleSwiftTests/testBasicSelectionActionAssert", "EarlGreyExampleSwiftTests/testBasicSelection")
        val result = Xctestrun.rewrite(xctestrun = swiftXctestrun, methods)
        val resultXML = Xctestrun.parse(result)
        val testTarget = resultXML["EarlGreyExampleSwiftTests"] as NSDictionary
        val resultMethods = testTarget["OnlyTestIdentifiers"] as NSArray

        assertThat(methods.toSet()).isEqualTo(resultMethods.array.map { it.toJavaObject() }.toSet())
    }

    @Test
    fun `rewrite methods in multiple test targets`() {
        val expectedMethods1 = listOf("SuiteA/testA1", "SuiteA/testA2")
        val expectedMethods2 = listOf("SwiftTests2/tests2_test1")

        val result = Xctestrun.rewrite(xctestrun = multipleTargetsSwiftXctestrun, listOf(expectedMethods1, expectedMethods2).flatMap { it })
        val resultXML = Xctestrun.parse(result)

        val targetSwiftTests1 = resultXML["SwiftTests1"] as NSDictionary
        val targetSwiftTests2 = resultXML["SwiftTests2"] as NSDictionary
        val resultMethods1 = targetSwiftTests1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = targetSwiftTests2["OnlyTestIdentifiers"] as NSArray

        assertThat(expectedMethods1.toSet()).isEqualTo(resultMethods1.array.map { it.toJavaObject() }.toSet())
        assertThat(expectedMethods2.toSet()).isEqualTo(resultMethods2.array.map { it.toJavaObject() }.toSet())
    }

    @Test
    fun `rewrite incorrect methods in multiple test targets`() {
        val methods1 = listOf("incorrect1", "incorrect2")
        val methods2 = listOf("incorrect3")

        val result = Xctestrun.rewrite(xctestrun = multipleTargetsSwiftXctestrun, listOf(methods1, methods2).flatMap { it })
        val resultXML = Xctestrun.parse(result)

        val targetSwiftTests1 = resultXML["SwiftTests1"] as NSDictionary
        val targetSwiftTests2 = resultXML["SwiftTests2"] as NSDictionary
        val resultMethods1 = targetSwiftTests1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = targetSwiftTests2["OnlyTestIdentifiers"] as NSArray

        assertThat(resultMethods1.array.isEmpty()).isTrue()
        assertThat(resultMethods2.array.isEmpty()).isTrue()
    }

    @Test
    fun `rewrite mix of correct and incorrect methods in multiple test targets`() {
        val methods1 = listOf("SuiteA/testA1", "SuiteA/testA2", "incorrect1")
        val methods2 = listOf("SwiftTests2/tests2_test1", "incorrect2")

        val expectedMethods1 = listOf("SuiteA/testA1", "SuiteA/testA2")
        val expectedMethods2 = listOf("SwiftTests2/tests2_test1")

        val result = Xctestrun.rewrite(xctestrun = multipleTargetsSwiftXctestrun, listOf(methods1, methods2).flatMap { it })
        val resultXML = Xctestrun.parse(result)

        val targetSwiftTests1 = resultXML["SwiftTests1"] as NSDictionary
        val targetSwiftTests2 = resultXML["SwiftTests2"] as NSDictionary
        val resultMethods1 = targetSwiftTests1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = targetSwiftTests2["OnlyTestIdentifiers"] as NSArray

        assertThat(expectedMethods1.toSet()).isEqualTo(resultMethods1.array.map { it.toJavaObject() }.toSet())
        assertThat(expectedMethods2.toSet()).isEqualTo(resultMethods2.array.map { it.toJavaObject() }.toSet())
    }
}
