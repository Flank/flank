package ftl.config

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule

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

    private val yamlFile = "./flank.yml"

    @Test
    fun configLoadsSuccessfully() {
        val config = YamlConfig.load(yamlFile)

        assert(config.appApk, "../../test_app/apks/app-debug.apk")
        assert(config.testApk, "../../test_app/apks/app-debug-androidTest.apk")
        assert(config.rootGcsBucket, "tmp_bucket_2")

        assert(config.useOrchestrator, true)
        assert(config.disablePerformanceMetrics, true)
        assert(config.disableVideoRecording, false)
        assert(config.testTimeoutMinutes, 60L)

        assert(config.shardCount, 1)
        assert(config.waitForResults, true)
        assert(config.testMethods, listOf("com.example.app.ExampleUiTest#testPasses"))
        assert(config.limitBreak, false)
    }

    @Test
    fun limitBreakFalseExitsOnLargeShards() {
        exit.expectSystemExitWithStatus(-1)

        val config = YamlConfig.load(yamlFile)
        config.shardCount = 99_999
    }

    @Test
    fun limitBreakTrueAllowsLargeShards() {
        val oldConfig = YamlConfig.load(yamlFile)
        val config = YamlConfig(
                oldConfig.appApk,
                oldConfig.testApk,
                oldConfig.rootGcsBucket,
                limitBreak = true)
        config.shardCount = 99_999
    }
}
