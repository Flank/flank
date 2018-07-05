package ftl.config

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosConfigTest {

    @Rule
    @JvmField
    val exit = ExpectedSystemExit.none()!!

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()!!

    private val yamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.ios.yml")
    private val yamlFile2 = getPath("src/test/kotlin/ftl/fixtures/flank2.ios.yml")
    private val xctestrunZip = getPath("src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip")
    private val xctestrunFile = getPath("src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleMixedTests_iphoneos11.2-arm64.xctestrun")
    private val testName = "EarlGreyExampleMixedTests/testBasicSelection"
    // NOTE: Change working dir to '%MODULE_WORKING_DIR%' in IntelliJ to match gradle for this test to pass.
    @Test
    fun configLoadsSuccessfully() {
        val config = IosConfig.load(yamlFile)

        assert(getPath(config.gCloudConfig.xctestrunZip), xctestrunZip)
        assert(getPath(config.gCloudConfig.xctestrunFile), xctestrunFile)

        with(config.gCloudConfig) {
            assert(rootGcsBucket, "tmp_bucket_2")
            assert(disablePerformanceMetrics, true)
            assert(disableRecordVideo, true)
            assert(testTimeout, "60m")
            assert(waitForResults, true)
            assert(testMethods, listOf(testName))
            assert(devices, listOf(
                    Device("iphone8", "11.2", "en_US", "portrait")
            ))
        }

        with(config.flankConfig) {
            assert(testShards, 1)
            assert(testRuns, 1)
            assert(limitBreak, false)
        }
    }

    @Test
    fun platformDisplayConfig() {
        val config = IosConfig.load(yamlFile)
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
        val config = IosConfig.load(yamlFile2)

        val chunk0 = arrayListOf("EarlGreyExampleMixedTests/testGrantCameraPermission",
                "EarlGreyExampleMixedTests/testGrantMicrophonePermission",
                "EarlGreyExampleMixedTests/testBasicSelection1",
                "EarlGreyExampleMixedTests/testBasicSelection2")

        val chunk1 = arrayListOf("EarlGreyExampleMixedTests/testGrantCameraPermission",
                "EarlGreyExampleMixedTests/testGrantMicrophonePermission",
                "EarlGreyExampleMixedTests/testBasicSelection3",
                "EarlGreyExampleMixedTests/testBasicSelection4")

        val testShardChunks = config.flankConfig.testShardChunks

        assertThat(testShardChunks.size).isEqualTo(2)
        assertThat(testShardChunks[0]).isEqualTo(chunk0)
        assertThat(testShardChunks[1]).isEqualTo(chunk1)
    }
}
