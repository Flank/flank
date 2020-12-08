package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants
import ftl.ios.xctest.common.findTestsForTestTarget
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.test.util.TestHelper.normalizeLineEnding
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File

class RewriteXcTestRunV1KtTest {

    @Test
    fun rewrite() {
        assumeFalse(FtlConstants.isWindows)

        val root = parseToNSDictionary(swiftXcTestRunV1)
        val methods = findTestsForTestTarget(testTarget = "EarlGreyExampleSwiftTests", xctestrun = File(swiftXcTestRunV1))
        val methodsData = mapOf("EarlGreyExampleSwiftTests" to methods)

        val results = String(rewriteXcTestRunV1(root, methodsData))

        assertThat(results.contains("OnlyTestIdentifiers")).isTrue()
    }

    @Test
    fun rewriteImmutable() {
        assumeFalse(FtlConstants.isWindows)

        val root = parseToNSDictionary(swiftXcTestRunV1)
        val methods = findTestsForTestTarget(testTarget = "EarlGreyExampleSwiftTests", xctestrun = File(swiftXcTestRunV1))
        val methodsData = mapOf("EarlGreyExampleSwiftTests" to methods)

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
        val methodsData = mapOf("EarlGreyExampleSwiftTests" to methods, "EarlGreyExampleTests" to methods)
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

        val tests = mapOf("EarlGreyExampleSwiftTests" to listOf("testOne", "testTwo"))
        val rewrittenXml = String(rewriteXcTestRunV1(root, tests))

        assertThat(inputXml).isEqualTo(rewrittenXml.normalizeLineEnding())
    }

    @Test
    fun `rewrite methods in single test target`() {
        assumeFalse(FtlConstants.isWindows)
        val methods = listOf("EarlGreyExampleSwiftTests/testBasicSelectionActionAssert", "EarlGreyExampleSwiftTests/testBasicSelection")
        val result = rewriteXcTestRunV1(xctestrun = swiftXcTestRunV1, methods)
        val resultXML = parseToNSDictionary(result)
        val testTarget = resultXML["EarlGreyExampleSwiftTests"] as NSDictionary
        val resultMethods = testTarget["OnlyTestIdentifiers"] as NSArray

        assertThat(methods.toSet()).isEqualTo(resultMethods.array.map { it.toJavaObject() }.toSet())
    }

    @Test
    fun `rewrite methods in multiple test targets`() {
        assumeFalse(FtlConstants.isWindows) // TODO enable it on #1180

        val expectedMethods1 = listOf("FlankExampleTests/test1", "FlankExampleTests/test2")
        val expectedMethods2 = listOf("FlankExampleSecondTests/test3")

        val result = rewriteXcTestRunV1(xctestrun = multiTargetsSwiftXcTestRunV1, listOf(expectedMethods1, expectedMethods2).flatMap { it })
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
        assumeFalse(FtlConstants.isWindows) // TODO enable it on #1180

        val methods1 = listOf("incorrect1", "incorrect2")
        val methods2 = listOf("incorrect3")

        val result = rewriteXcTestRunV1(xctestrun = multiTargetsSwiftXcTestRunV1, listOf(methods1, methods2).flatMap { it })
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
        assumeFalse(FtlConstants.isWindows) // TODO enable it on #1180

        val methods1 = listOf("FlankExampleTests/test1", "FlankExampleTests/test2", "incorrect1")
        val methods2 = listOf("FlankExampleSecondTests/test3", "incorrect2")

        val expectedMethods1 = listOf("FlankExampleTests/test1", "FlankExampleTests/test2")
        val expectedMethods2 = listOf("FlankExampleSecondTests/test3")

        val result = rewriteXcTestRunV1(xctestrun = multiTargetsSwiftXcTestRunV1, listOf(methods1, methods2).flatMap { it })
        val resultXML = parseToNSDictionary(result)

        val target1 = resultXML["FlankExampleTests"] as NSDictionary
        val target2 = resultXML["FlankExampleSecondTests"] as NSDictionary
        val resultMethods1 = target1["OnlyTestIdentifiers"] as NSArray
        val resultMethods2 = target2["OnlyTestIdentifiers"] as NSArray

        assertThat(expectedMethods1.toSet()).isEqualTo(resultMethods1.array.map { it.toJavaObject() }.toSet())
        assertThat(expectedMethods2.toSet()).isEqualTo(resultMethods2.array.map { it.toJavaObject() }.toSet())
    }
}
