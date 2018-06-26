package ftl.config

import ftl.TestArtifact
import ftl.run.TestRunner.bitrise
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import java.nio.file.Paths

class IosConfigTest: TestArtifact() {

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

    private val yamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.ios.yml")
    private val xctestrunZip = getPath("src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip")
    private val xctestrunFile = getPath("src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleMixedTests_iphoneos11.2-arm64.xctestrun")
    private val testName = "EarlGreyExampleMixedTests/testBasicSelection"
    // NOTE: Change working dir to '%MODULE_WORKING_DIR%' in IntelliJ to match gradle for this test to pass.
    @Test
    fun configLoadsSuccessfully() {
        val config = IosConfig.load(yamlFile)

        assert(getPath(config.xctestrunZip), xctestrunZip)
        assert(getPath(config.xctestrunFile), xctestrunFile)
        assert(config.rootGcsBucket, "tmp_bucket_2")

        assert(config.disablePerformanceMetrics, true)
        assert(config.disableVideoRecording, false)
        assert(config.testTimeoutMinutes, 60L)

        assert(config.testShards, 1)
        assert(config.testRuns, 1)
        assert(config.waitForResults, true)
        assert(config.testMethods, listOf(testName))
        assert(config.limitBreak, false)
        assert(config.devices, listOf(
            Device("iphone8", "11.2", "en_US", "portrait")
        ))
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
}
