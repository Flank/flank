package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.ios.xctest.common.ONLY_TEST_IDENTIFIERS
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test

class ReduceXcTestRunV2KtTest {

    @Test
    fun rewrite() {
        assumeFalse(FtlConstants.isWindows)
        // given
        val uiTestsMethods = listOf("UITestsClass/test1_1")
        val expected = listOf(
            """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>TestConfigurations</key>
	<array>
		<dict>
			<key>Name</key>
			<string>en</string>
			<key>TestTargets</key>
			<array>
				<dict>
					<key>BlueprintName</key>
					<string>UITests</string>
					<key>BundleIdentifiersForCrashReportEmphasis</key>
					<array>
						<string>io.gogoapps.FlankMultiTestTargetsExample</string>
						<string>io.gogoapps.SecondUITests</string>
						<string>io.gogoapps.UITests</string>
					</array>
					<key>CommandLineArguments</key>
					<array>
						<string>-AppleLanguages</string>
						<string>(en)</string>
						<string>-AppleTextDirection</string>
						<string>NO</string>
						<string>-AppleLocale</string>
						<string>en_GB</string>
					</array>
					<key>DefaultTestExecutionTimeAllowance</key>
					<integer>600</integer>
					<key>DependentProductPaths</key>
					<array>
						<string>__TESTROOT__/Debug-iphoneos/UITests-Runner.app/PlugIns/UITests.xctest</string>
						<string>__TESTROOT__/Debug-iphoneos/SecondUITests-Runner.app/PlugIns/SecondUITests.xctest</string>
						<string>__TESTROOT__/Debug-iphoneos/FlankTestPlansExample.app</string>
					</array>
					<key>EnvironmentVariables</key>
					<dict>
						<key>OS_ACTIVITY_DT_MODE</key>
						<string>YES</string>
						<key>SQLITE_ENABLE_THREAD_ASSERTIONS</key>
						<string>1</string>
					</dict>
					<key>IsUITestBundle</key>
					<true/>
					<key>IsXCTRunnerHostedTestBundle</key>
					<true/>
					<key>ProductModuleName</key>
					<string>UITests</string>
					<key>SystemAttachmentLifetime</key>
					<string>deleteOnSuccess</string>
					<key>TestBundlePath</key>
					<string>__TESTHOST__/PlugIns/UITests.xctest</string>
					<key>TestHostBundleIdentifier</key>
					<string>io.gogoapps.UITests.xctrunner</string>
					<key>TestHostPath</key>
					<string>__TESTROOT__/Debug-iphoneos/UITests-Runner.app</string>
					<key>TestLanguage</key>
					<string>en</string>
					<key>TestRegion</key>
					<string>GB</string>
					<key>TestTimeoutsEnabled</key>
					<false/>
					<key>TestingEnvironmentVariables</key>
					<dict>
					</dict>
					<key>ToolchainsSettingValue</key>
					<array>
					</array>
					<key>UITargetAppCommandLineArguments</key>
					<array>
						<string>-AppleLanguages</string>
						<string>(en)</string>
						<string>-AppleTextDirection</string>
						<string>NO</string>
						<string>-AppleLocale</string>
						<string>en_GB</string>
					</array>
					<key>UITargetAppEnvironmentVariables</key>
					<dict>
					</dict>
					<key>UITargetAppPath</key>
					<string>__TESTROOT__/Debug-iphoneos/FlankTestPlansExample.app</string>
					<key>UserAttachmentLifetime</key>
					<string>deleteOnSuccess</string>
					<key>OnlyTestIdentifiers</key>
					<array>
						<string>UITestsClass/test1_1</string>
					</array>
				</dict>
			</array>
		</dict>
	</array>
	<key>TestPlan</key>
	<dict>
		<key>IsDefault</key>
		<true/>
		<key>Name</key>
		<string>AllTests</string>
	</dict>
	<key>__xctestrun_metadata__</key>
	<dict>
		<key>FormatVersion</key>
		<integer>2</integer>
	</dict>
</dict>
</plist>
            """.trimIndent(),
            """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>TestConfigurations</key>
	<array>
		<dict>
			<key>Name</key>
			<string>pl</string>
			<key>TestTargets</key>
			<array>
				<dict>
					<key>BlueprintName</key>
					<string>UITests</string>
					<key>BundleIdentifiersForCrashReportEmphasis</key>
					<array>
						<string>io.gogoapps.FlankMultiTestTargetsExample</string>
						<string>io.gogoapps.SecondUITests</string>
						<string>io.gogoapps.UITests</string>
					</array>
					<key>CommandLineArguments</key>
					<array>
						<string>-AppleLanguages</string>
						<string>(pl)</string>
						<string>-AppleTextDirection</string>
						<string>NO</string>
						<string>-AppleLocale</string>
						<string>pl_PL</string>
					</array>
					<key>DefaultTestExecutionTimeAllowance</key>
					<integer>600</integer>
					<key>DependentProductPaths</key>
					<array>
						<string>__TESTROOT__/Debug-iphoneos/UITests-Runner.app/PlugIns/UITests.xctest</string>
						<string>__TESTROOT__/Debug-iphoneos/SecondUITests-Runner.app/PlugIns/SecondUITests.xctest</string>
						<string>__TESTROOT__/Debug-iphoneos/FlankTestPlansExample.app</string>
					</array>
					<key>EnvironmentVariables</key>
					<dict>
						<key>OS_ACTIVITY_DT_MODE</key>
						<string>YES</string>
						<key>SQLITE_ENABLE_THREAD_ASSERTIONS</key>
						<string>1</string>
					</dict>
					<key>IsUITestBundle</key>
					<true/>
					<key>IsXCTRunnerHostedTestBundle</key>
					<true/>
					<key>ProductModuleName</key>
					<string>UITests</string>
					<key>SystemAttachmentLifetime</key>
					<string>deleteOnSuccess</string>
					<key>TestBundlePath</key>
					<string>__TESTHOST__/PlugIns/UITests.xctest</string>
					<key>TestHostBundleIdentifier</key>
					<string>io.gogoapps.UITests.xctrunner</string>
					<key>TestHostPath</key>
					<string>__TESTROOT__/Debug-iphoneos/UITests-Runner.app</string>
					<key>TestLanguage</key>
					<string>pl</string>
					<key>TestRegion</key>
					<string>PL</string>
					<key>TestTimeoutsEnabled</key>
					<false/>
					<key>TestingEnvironmentVariables</key>
					<dict>
					</dict>
					<key>ToolchainsSettingValue</key>
					<array>
					</array>
					<key>UITargetAppCommandLineArguments</key>
					<array>
						<string>-AppleLanguages</string>
						<string>(pl)</string>
						<string>-AppleTextDirection</string>
						<string>NO</string>
						<string>-AppleLocale</string>
						<string>pl_PL</string>
					</array>
					<key>UITargetAppEnvironmentVariables</key>
					<dict>
					</dict>
					<key>UITargetAppPath</key>
					<string>__TESTROOT__/Debug-iphoneos/FlankTestPlansExample.app</string>
					<key>UserAttachmentLifetime</key>
					<string>deleteOnSuccess</string>
					<key>OnlyTestIdentifiers</key>
					<array>
						<string>UITestsClass/test1_1</string>
					</array>
				</dict>
			</array>
		</dict>
	</array>
	<key>TestPlan</key>
	<dict>
		<key>IsDefault</key>
		<true/>
		<key>Name</key>
		<string>AllTests</string>
	</dict>
	<key>__xctestrun_metadata__</key>
	<dict>
		<key>FormatVersion</key>
		<integer>2</integer>
	</dict>
</dict>
</plist>
            """.trimIndent()
        )

        // when
        val result: List<String> = runBlocking {
            IosArgs.default()
                .copy(
                    xctestrunFile = swiftXcTestRunV2,
                    testTargets = uiTestsMethods
                )
                .xcTestRunFlow()
                .map { String(it) }
                .toList()
        }

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `ensure rewrite not mutates root`() {
        assumeFalse(FtlConstants.isWindows)

        // given
        val args = IosArgs.default()
            .copy(
                xctestrunFile = swiftXcTestRunV2,
                testTargets = emptyList()
            )

        assertThat(
            args.xcTestRunData.nsDict.toASCIIPropertyList().contains(ONLY_TEST_IDENTIFIERS)
        ).isFalse()

        // when
        runBlocking { args.xcTestRunFlow().toList() }

        // then
        assertThat(
            args.xcTestRunData.nsDict.toASCIIPropertyList().contains(ONLY_TEST_IDENTIFIERS)
        ).isFalse()
    }
}
