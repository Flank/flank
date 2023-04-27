package ftl.args

import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import ftl.config.Device
import ftl.ios.xctest.flattenShardChunks
import ftl.run.exception.FlankGeneralError
import ftl.run.status.OutputStyle
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosArgsFileTest {

    @get:Rule
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()

    @get:Rule
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()

    private val ymlNotFound = getPath("not_found.yml")
    private val yamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.ios.yml")
    private val yamlFile2 = getPath("src/test/kotlin/ftl/fixtures/flank2.ios.yml")
    private val xctestrunZip = getPath("src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip")
    private val xctestrunFile =
        getPath("src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun")
    private val testName = "EarlGreyExampleSwiftTests/testLayout"
    // NOTE: Change working dir to '%MODULE_WORKING_DIR%' in IntelliJ to match gradle for this test to pass.
    @Test
    fun configLoadsSuccessfully() {
        val config = IosArgs.load(yamlFile)

        assert(getPath(config.xctestrunZip), xctestrunZip)
        assert(getPath(config.xctestrunFile), xctestrunFile)

        with(config) {
            assert(resultsBucket, "tmp_bucket_2")
            assert(recordVideo, true)
            assert(testTimeout, "60m")
            assert(async, true)
            assert(testTargets, listOf(testName))
            assert(
                devices,
                listOf(
                    Device("iphone13pro", "15.7", "en_US", "portrait")
                )
            )
            assert(maxTestShards, 1)
            assert(repeatTests, 1)
        }
    }

    @Test
    fun platformDisplayConfig() {
        val config = IosArgs.load(yamlFile)
        val iosConfig = config.toString()
        assert(iosConfig.contains("appApk"), false)
        assert(iosConfig.contains("testApk"), false)
        assert(iosConfig.contains("autoGoogleLogin"), false)
        assert(iosConfig.contains("useOrchestrator"), false)
        assert(iosConfig.contains("environmentVariables"), false)
        assert(iosConfig.contains("directoriesToPull"), false)
    }

    @Test
    fun testMethodsAlwaysRun() {
        Assume.assumeFalse(isWindows)

        val config = IosArgs.load(yamlFile2)

        val chunk0 = arrayListOf(
            "EarlGreyExampleSwiftTests/testWithGreyAssertions",
            "EarlGreyExampleSwiftTests/testWithInRoot",
            "EarlGreyExampleSwiftTests/testWithCondition",
            "EarlGreyExampleSwiftTests/testWithCustomFailureHandler"
        )

        val chunk1 = arrayListOf(
            "EarlGreyExampleSwiftTests/testWithGreyAssertions",
            "EarlGreyExampleSwiftTests/testWithCustomMatcher",
            "EarlGreyExampleSwiftTests/testWithCustomAssertion"
        )

        val testShardChunks = config.xcTestRunData.flattenShardChunks()

        assertThat(testShardChunks.size).isEqualTo(2)
        assertThat(testShardChunks[0].testMethodNames).isEqualTo(chunk0)
        assertThat(testShardChunks[1].testMethodNames).isEqualTo(chunk1)
    }

    @Test
    fun `verify run timeout value from yml file`() {
        val args = IosArgs.load(yamlFile)
        assertEquals(60 * 60 * 1000L, args.parsedTimeout)
    }

    @Test
    fun `verify output style value from uml file`() {
        val args = IosArgs.load(yamlFile)
        assertEquals(OutputStyle.Single, args.outputStyle)
    }

    @Test(expected = FlankGeneralError::class)
    fun `should throw if load and yamlFile not found`() {
        IosArgs.load(ymlNotFound)
    }

    @Test
    fun `should not throw if loadOrDefault and yamlFile not found`() {
        IosArgs.loadOrDefault(ymlNotFound)
    }
}
