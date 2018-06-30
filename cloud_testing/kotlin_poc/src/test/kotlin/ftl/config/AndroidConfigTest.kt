package ftl.config

import ftl.run.TestRunner.bitrise
import ftl.android.LocalGcs
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import java.nio.file.Paths

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

    private fun assert(actual: Any, expected: Any) =
            assertEquals(expected, actual)

    private fun getPath(path: String): String =
            Paths.get(path).normalize().toAbsolutePath().toString()

    private val localYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.local.yml")
    private val gcsYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.gcs.yml")
    private val appApkLocal = getPath("../../test_app/apks/app-debug.apk")
    private val appApkGcs = "gs://tmp_bucket_2/app-debug.apk"
    private val testApkLocal = getPath("../../test_app/apks/app-debug-androidTest.apk")
    private val testApkGcs = "gs://tmp_bucket_2/app-debug-androidTest.apk"
    private val testName = "class com.example.app.ExampleUiTest#testPasses"
    private val directoryToPull = "/sdcard/screenshots"

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

    private fun checkConfig(config: AndroidConfig, local: Boolean) {
        if (local) assert(getPath(config.testApk), testApkLocal)
        else assert(config.testApk, testApkGcs)

        if (local) assert(getPath(config.appApk), appApkLocal)
        else assert(config.appApk, appApkGcs)

        assert(config.rootGcsBucket, LocalGcs.TEST_BUCKET)

        assert(config.autoGoogleLogin, true)
        assert(config.useOrchestrator, true)
        assert(config.disablePerformanceMetrics, true)
        assert(config.disableVideoRecording, false)
        assert(config.testTimeoutMinutes, 60L)

        assert(config.testShards, 1)
        assert(config.testRuns, 1)
        assert(config.waitForResults, true)
        assert(config.testMethods, listOf(testName))
        assert(config.limitBreak, false)
        assert(config.devices, listOf(
                Device("NexusLowRes", "23", "en", "portrait"),
                Device("NexusLowRes", "23", "en", "landscape"),
                Device("shamu", "22", "zh_CN", "default")))
        assert(config.environmentVariables, mapOf(Pair("clearPackageData", "true")))
        assert(config.directoriesToPull, listOf(directoryToPull))

    }

    private val s99_999 = 99_999

    @Test
    fun limitBreakFalseExitsOnLargeShards() {
        exit.expectSystemExitWithStatus(-1)

        val config = AndroidConfig.load(localYamlFile)
        config.testRuns = s99_999
        config.testShards = s99_999
        assert(config.testRuns, s99_999)
        assert(config.testShards, s99_999)
    }

    @Test
    fun limitBreakTrueAllowsLargeShards() {
        val oldConfig = AndroidConfig.load(localYamlFile)
        val config = AndroidConfig(
                oldConfig.appApk,
                oldConfig.testApk,
                rootGcsBucket = oldConfig.rootGcsBucket,
                limitBreak = true)
        config.testRuns = s99_999
        config.testShards = s99_999
        assert(config.testRuns, s99_999)
        assert(config.testShards, s99_999)
    }

    private fun configWithTestMethods(amount: Int, testShards: Int = 1): YamlConfig {
        val testMethods = mutableListOf<String>()
        // test names must be unique otherwise the Set<String> will add them only once.
        repeat(amount) { index -> testMethods.add(testName + index) }

        return AndroidConfig(
                appApk = appApkLocal,
                testApk = testApkLocal,
                rootGcsBucket = "",
                testShards = testShards,
                testMethods = testMethods
        )
    }

    @Test
    fun calculateShards() {
        var config = configWithTestMethods(1)
        assert(config.testShards, 1)
        assert(config.testShardChunks.size, 1)
        assert(config.testShardChunks.first().size, 1)

        config = configWithTestMethods(155)
        assert(config.testShards, 1)
        assert(config.testShardChunks.size, 1)
        assert(config.testShardChunks.first().size, 155)

        config = configWithTestMethods(155, testShards = 40)
        assert(config.testShards, 40)
        assert(config.testShardChunks.size, 39)
        assert(config.testShardChunks.first().size, 4)
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

        val oldConfig = AndroidConfig.load(localYamlFile)
        // Need to set the project id to get the bucket info from StorageOptions
        val config = AndroidConfig(
                oldConfig.appApk,
                oldConfig.testApk,
                rootGcsBucket = oldConfig.rootGcsBucket,
                projectId = "delta-essence-114723",
                limitBreak = true)

        assert(config.getGcsBucket(), "tmp_bucket_2")
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            FtlConstants.useMock = true
            LocalGcs.setupApks()
        }
    }
}
