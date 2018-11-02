package ftl.ios

import com.dd.plist.NSDictionary
import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestArtifact.fixturesPath
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class XctestrunTest {

    private val swiftXctestrun = "$fixturesPath/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun"
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
        assertThat(dict.count()).isEqualTo(15)
        assertThat(dict.containsKey("OnlyTestIdentifiers")).isFalse()
    }

    @Test(expected = RuntimeException::class)
    fun parse_fileNotFound() {
        Xctestrun.parse("./XctestrunThatDoesNotExist")
    }

    @Test
    fun findTestNames() {
        val names = Xctestrun.findTestNames(swiftXctestrun).sorted()
        assertThat(swiftTests).isEqualTo(names)
    }

    @Test
    fun rewrite() {
        val root = Xctestrun.parse(swiftXctestrun)
        val methods = Xctestrun.findTestNames(swiftXctestrun)
        val results = String(Xctestrun.rewrite(root, methods))
        assertThat(results.contains("OnlyTestIdentifiers")).isTrue()
    }

    @Test
    fun rewriteImmutable() {
        val root = Xctestrun.parse(swiftXctestrun)
        val methods = Xctestrun.findTestNames(swiftXctestrun)

        // ensure root object isn't modified. Rewrite should return a new object.
        val key = "OnlyTestIdentifiers"

        assertThat(root.toASCIIPropertyList().contains(key)).isFalse()

        Xctestrun.rewrite(root, methods)

        assertThat(root.toASCIIPropertyList().contains(key)).isFalse()
    }

    @Test
    fun rewrite_idempotent() {
        val root = NSDictionary()
        root["EarlGreyExampleSwiftTests"] = NSDictionary()
        root["EarlGreyExampleTests"] = NSDictionary()
        val methods = setOf("testOne", "testTwo")
        Xctestrun.rewrite(root, methods)
        Xctestrun.rewrite(root, methods)
        val result = Xctestrun.rewrite(root, methods)
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
        assertThat(expected).isEqualTo(String(result))
    }
}
