package ftl.config

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import java.nio.file.Paths

class YamlConfigTest {

    init {
        FtlConstants.useMock = true
    }

    @Rule
    @JvmField
    val exit = ExpectedSystemExit.none()!!

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()!!

    private fun assert(actual: Any, expected: Any) {
        assertEquals(expected, actual)
    }

    private fun getPath(path: String): String {
        return Paths.get(path).normalize().toAbsolutePath().toString()
    }

    private val yamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.yml")
    private val appApk = getPath("../../test_app/apks/app-debug.apk")
    private val testApk = getPath("../../test_app/apks/app-debug-androidTest.apk")
    private val testName = "com.example.app.ExampleUiTest#testPasses"

    // NOTE: Change working dir to '%MODULE_WORKING_DIR%' in IntelliJ to match gradle for this test to pass.
    @Test
    fun configLoadsSuccessfully() {
        val config = YamlConfig.load(yamlFile)

        assert(getPath(config.appApk), appApk)
        assert(getPath(config.testApk), testApk)
        assert(config.rootGcsBucket, "tmp_bucket_2")

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
                Devices("NexusLowRes", "23", "en", "portrait"),
                Devices("NexusLowRes", "23", "en", "landscape"),
                Devices("shamu", "22", "zh_CN", "default")))
        assert(config.environmentVariables, mapOf(Pair("clearPackageData", "true")))
    }

    private val s99_999 = 99_999

    @Test
    fun limitBreakFalseExitsOnLargeShards() {
        exit.expectSystemExitWithStatus(-1)

        val config = YamlConfig.load(yamlFile)
        config.testRuns = s99_999
        config.testShards = s99_999
        assert(config.testRuns, s99_999)
        assert(config.testShards, s99_999)
    }

    @Test
    fun limitBreakTrueAllowsLargeShards() {
        val oldConfig = YamlConfig.load(yamlFile)
        val config = YamlConfig(
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

        return YamlConfig(
                appApk = appApk,
                testApk = testApk,
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
        val config = YamlConfig.load(yamlFile)

        if (config.iOS()) {
            val iosConfig = config.toString()
            assert(iosConfig.contains("appApk"), false)
            assert(iosConfig.contains("testApk"), false)
            assert(iosConfig.contains("autoGoogleLogin"), false)
            assert(iosConfig.contains("useOrchestrator"), false)
            assert(iosConfig.contains("testShards"), false)
            assert(iosConfig.contains("testMethods"), false)
        } else {
            val androidConfig = config.toString()
            assert(androidConfig.contains("xctestrunZip"), false)
            assert(androidConfig.contains("xctestrunFile"), false)
        }
    }
}
