package ftl.args

import ftl.args.yml.AndroidFlankYml
import ftl.args.yml.AndroidGcloudYml
import ftl.args.yml.AndroidGcloudYmlParams
import ftl.args.yml.FlankYml
import ftl.args.yml.FlankYmlParams
import ftl.args.yml.GcloudYml
import ftl.args.yml.GcloudYmlParams
import ftl.config.Device
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import ftl.test.util.TestHelper.getString
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class AndroidArgsFileTest {

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().muteForSuccessfulTests()!!

    private val localYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.local.yml")
    private val gcsYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.gcs.yml")
    private val appApkLocal = getString("../test_app/apks/app-debug.apk")
    private val appApkGcs = "gs://tmp_bucket_2/app-debug.apk"
    private val testApkLocal = getString("../test_app/apks/app-debug-androidTest.apk")
    private val testApkGcs = "gs://tmp_bucket_2/app-debug-androidTest.apk"
    private val testName = "class com.example.app.ExampleUiTest#testPasses"
    private val directoryToPull = "/sdcard/screenshots"

    private val appApkAbsolutePath = appApkLocal.absolutePath()
    private val testApkAbsolutePath = testApkLocal.absolutePath()

    // NOTE: Change working dir to '%MODULE_WORKING_DIR%' in IntelliJ to match gradle for this test to pass.
    @Test
    fun localConfigLoadsSuccessfully() {
        val config = AndroidArgs.load(localYamlFile)
        checkConfig(config, true)
    }

    @Test
    fun gcsConfigLoadsSuccessfully() {
        val config = AndroidArgs.load(gcsYamlFile)
        checkConfig(config, false)
    }

    private fun checkConfig(args: AndroidArgs, local: Boolean) {

        with(args) {
            if (local) assert(getString(testApk!!), getString(testApkAbsolutePath))
            else assert(testApk, testApkGcs)

            if (local) assert(getString(appApk), getString(appApkAbsolutePath))
            else assert(appApk, appApkGcs)

            assert(autoGoogleLogin, true)
            assert(useOrchestrator, true)
            assert(environmentVariables, mapOf("clearPackageData" to "true"))
            assert(directoriesToPull, listOf(directoryToPull))
            assert(resultsBucket, "tmp_bucket_2")
            assert(performanceMetrics, true)
            assert(recordVideo, true)
            assert(testTimeout, "60m")
            assert(async, true)
            assert(testTargets, listOf(testName))
            assert(
                devices, listOf(
                    Device("NexusLowRes", "23", "en", "portrait"),
                    Device("NexusLowRes", "23", "en", "landscape"),
                    Device("shamu", "22", "zh_CN", "default")
                )
            )
            assert(maxTestShards, 1)
            assert(repeatTests, 1)
        }
    }

    private fun configWithTestMethods(amount: Int, maxTestShards: Int = 1): AndroidArgs {

        return AndroidArgs(
            GcloudYml(GcloudYmlParams()),
            AndroidGcloudYml(
                AndroidGcloudYmlParams(
                    app = appApkLocal,
                    test = getString("src/test/kotlin/ftl/fixtures/tmp/apk/app-debug-androidTest_$amount.apk")
                )
            ),
            FlankYml(
                FlankYmlParams(
                    maxTestShards = maxTestShards
                )
            ),
            AndroidFlankYml(),
            ""
        )
    }

    @Test
    fun `calculateShards 0`() {
        val config = configWithTestMethods(0)
        val testShardChunks = AndroidTestShard.getTestShardChunks(config, config.testApk!!)
        with(config) {
            assert(maxTestShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 0)
        }
    }

    @Test
    fun `calculateShards 1`() {
        val config = configWithTestMethods(1)
        val testShardChunks = AndroidTestShard.getTestShardChunks(config, config.testApk!!)
        with(config) {
            assert(maxTestShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 1)
        }
    }

    @Test
    fun `calculateShards 155`() {
        val config = configWithTestMethods(155)
        val testShardChunks = AndroidTestShard.getTestShardChunks(config, config.testApk!!)
        with(config) {
            assert(maxTestShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 155)
        }
    }

    @Test
    fun `calculateShards 155 40`() {
        val config = configWithTestMethods(155, maxTestShards = 40)
        val testShardChunks = AndroidTestShard.getTestShardChunks(config, config.testApk!!)
        with(config) {
            assert(maxTestShards, 40)
            assert(testShardChunks.size, 40)
            assert(testShardChunks.first().size, 3)
        }
    }

    @Test
    fun platformDisplayConfig() {
        val androidConfig = AndroidArgs.load(localYamlFile).toString()
        assert(androidConfig.contains("xctestrunZip"), false)
        assert(androidConfig.contains("xctestrunFile"), false)
    }

    @Test
    fun assertGcsBucket() {
        val oldConfig = AndroidArgs.load(localYamlFile)
        // Need to set the project id to get the bucket info from StorageOptions
        val config = AndroidArgs(
            GcloudYml(
                GcloudYmlParams(
                    resultsBucket = oldConfig.resultsBucket
                )
            ),
            AndroidGcloudYml(
                AndroidGcloudYmlParams(
                    app = oldConfig.appApk,
                    test = oldConfig.testApk
                )
            ),
            FlankYml(
                FlankYmlParams(
                    project = "flank-open-source"
                )
            ),
            AndroidFlankYml(),
            ""
        )

        assert(config.resultsBucket, "tmp_bucket_2")
    }

    @Test
    fun `verify run timeout value from yml file`() {
        val args = AndroidArgs.load(localYamlFile)
        assertEquals(60 * 60 * 1000L, args.parsedTimeout)
    }
}
