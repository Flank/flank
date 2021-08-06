package ftl.ios.xctest.common

import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import ftl.ios.xctest.FIXTURES_PATH
import ftl.ios.xctest.multiTargetsSwiftXcTestRunV1
import ftl.ios.xctest.swiftTestsV1
import ftl.ios.xctest.swiftXcTestRunV1
import ftl.run.exception.FlankGeneralError
import org.junit.Assume.assumeFalse
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FindTestsForTargetKtTest {

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

        val tmpXml = Paths.get("$FIXTURES_PATH/ios/EarlGreyExample", "skip.xctestrun")
        Files.write(tmpXml, inputXml.toByteArray())
        tmpXml.toFile().deleteOnExit()

        val actualTests = findTestsForTestTarget("EarlGreyExampleSwiftTests", tmpXml.toFile(), true).sorted()
        assertThat(actualTests).isEqualTo(listOf("EarlGreyExampleSwiftTests/testBasicSelection"))
    }

    @Test
    fun findTestNamesForTestTarget() {
        assumeFalse(isWindows)
        val names = findTestsForTestTarget(testTarget = "EarlGreyExampleSwiftTests", xctestrun = File(swiftXcTestRunV1), true).sorted()
        assertThat(swiftTestsV1).isEqualTo(names)
    }

    @Test(expected = FlankGeneralError::class)
    fun `findTestNames for nonexisting test target`() {
        assumeFalse(isWindows)
        findTestsForTestTarget(testTarget = "Incorrect", xctestrun = File(swiftXcTestRunV1), true).sorted()
    }

    @Test
    fun `find test names for xctestrun file containing multiple test targets`() {
        assumeFalse(isWindows)
        val names = findTestsForTestTarget(testTarget = "FlankExampleTests", xctestrun = File(multiTargetsSwiftXcTestRunV1), true).sorted()
        assertThat(names).isEqualTo(listOf("FlankExampleTests/test1", "FlankExampleTests/test2"))

        val names2 = findTestsForTestTarget(testTarget = "FlankExampleSecondTests", xctestrun = File(multiTargetsSwiftXcTestRunV1), true).sorted()
        assertThat(names2).isEqualTo(listOf("FlankExampleSecondTests/test3", "FlankExampleSecondTests/test4"))
    }
}
