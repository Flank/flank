package ftl.config

import ftl.run.TestRunner.bitrise
import ftl.test.util.FlankTestRunner
import ftl.test.util.LocalGcs
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class AndroidConfigTest {

    @Rule
    @JvmField
    val exit = ExpectedSystemExit.none()!!

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()!!

    private val localYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.local.yml")
    private val gcsYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.gcs.yml")
    private val appApkLocal = getPath("../../test_app/apks/app-debug.apk")
    private val appApkGcs = "gs://tmp_bucket_2/app-debug.apk"
    private val testApkLocal = getPath("../../test_app/apks/app-debug-androidTest.apk")
    private val testApkGcs = "gs://tmp_bucket_2/app-debug-androidTest.apk"
    private val testName = "class com.example.app.ExampleUiTest#testPasses"
    private val directoryToPull = "/sdcard/screenshots"

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            LocalGcs.setupApks()
        }
    }

    // NOTE: Change working dir to '%MODULE_WORKING_DIR%' in IntelliJ to match gradle for this test to pass.
    @Test
    fun localConfigLoadsSuccessfully() {
        val config = AndroidConfig.load(localYamlFile)
        checkConfig(config, true)
    }

    @Test
    fun gcsConfigLoadsSuccessfully() {
        val config = AndroidConfig.load(gcsYamlFile)
        checkConfig(config, false)
    }

    private fun checkConfig(config: YamlConfig<AndroidConfig>, local: Boolean) {

        with(config.gCloudConfig) {
            if (local) assert(getPath(testApk), testApkLocal)
            else assert(testApk, testApkGcs)

            if (local) assert(getPath(appApk), appApkLocal)
            else assert(appApk, appApkGcs)

            assert(autoGoogleLogin, true)
            assert(useOrchestrator, true)
            assert(environmentVariables, mapOf(Pair("clearPackageData", "true")))
            assert(directoriesToPull, listOf(directoryToPull))
            assert(rootGcsBucket, LocalGcs.TEST_BUCKET)
            assert(disablePerformanceMetrics, true)
            assert(disableRecordVideo, true)
            assert(testTimeout, "60m")
            assert(waitForResults, true)
            assert(testMethods, listOf(testName))
            assert(devices, listOf(
                    Device("NexusLowRes", "23", "en", "portrait"),
                    Device("NexusLowRes", "23", "en", "landscape"),
                    Device("shamu", "22", "zh_CN", "default")))
        }

        with(config.flankConfig) {
            assert(testShards, 1)
            assert(testRuns, 1)
            assert(limitBreak, false)
        }
    }

    @Suppress("PrivatePropertyName")
    private val s99_999 = 99_999

    @Test
    fun limitBreakFalseExitsOnLargeShards() {
        exit.expectSystemExitWithStatus(-1)

        val config = AndroidConfig.load(localYamlFile)
        with(config.flankConfig) {
            testRuns = s99_999
            testShards = s99_999
            assert(testRuns, s99_999)
            assert(testShards, s99_999)
        }
    }

    @Test
    fun limitBreakTrueAllowsLargeShards() {
        val oldConfig = AndroidConfig.load(localYamlFile).gCloudConfig
        val config = YamlConfig(
                AndroidConfig(
                        oldConfig.appApk,
                        oldConfig.testApk,
                        rootGcsBucket = oldConfig.rootGcsBucket),
                FlankConfig(
                        limitBreak = true
                )
        )

        with(config.flankConfig) {
            testRuns = s99_999
            testShards = s99_999
            assert(testRuns, s99_999)
            assert(testShards, s99_999)
        }
    }

    private fun configWithTestMethods(amount: Int, testShards: Int = 1): YamlConfig<AndroidConfig> {
        val testMethods = mutableListOf<String>()
        // test names must be unique otherwise the Set<String> will add them only once.
        repeat(amount) { index -> testMethods.add(testName + index) }

        return YamlConfig(
                AndroidConfig(
                        appApk = appApkLocal,
                        testApk = testApkLocal,
                        rootGcsBucket = "",
                        testMethods = testMethods),
                FlankConfig(
                        testShards = testShards
                )
        )
    }

    @Test
    fun calculateShards() {
        var config = configWithTestMethods(1)
        with(config.flankConfig) {
            assert(testShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 1)
        }

        config = configWithTestMethods(155)
        with(config.flankConfig) {
            assert(testShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 155)
        }

        config = configWithTestMethods(155, testShards = 40)
        with(config.flankConfig) {
            assert(testShards, 40)
            assert(testShardChunks.size, 39)
            assert(testShardChunks.first().size, 4)
        }
    }

    @Test
    fun platformDisplayConfig() {
        val androidConfig = AndroidConfig.load(localYamlFile).toString()
        assert(androidConfig.contains("xctestrunZip"), false)
        assert(androidConfig.contains("xctestrunFile"), false)
    }

    @Test
    fun assertGcsBucket() {
        if (bitrise) return

        val oldConfig = AndroidConfig.load(localYamlFile).gCloudConfig
        // Need to set the project id to get the bucket info from StorageOptions
        val config = YamlConfig(
                AndroidConfig(
                        oldConfig.appApk,
                        oldConfig.testApk,
                        rootGcsBucket = oldConfig.rootGcsBucket,
                        projectId = "delta-essence-114723"),
                FlankConfig(
                        limitBreak = true
                )
        )

        assert(config.getGcsBucket(), "tmp_bucket_2")
    }
}
