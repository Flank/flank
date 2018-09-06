package ftl.args

import ftl.args.yml.AndroidGcloudYml
import ftl.args.yml.AndroidGcloudYmlParams
import ftl.args.yml.FlankYml
import ftl.args.yml.FlankYmlParams
import ftl.args.yml.GcloudYml
import ftl.args.yml.GcloudYmlParams
import ftl.config.Device
import ftl.run.TestRunner.bitrise
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import ftl.test.util.TestHelper.getString
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

    private fun checkConfig(config: AndroidArgs, local: Boolean) {

        with(config) {
            if (local) assert(getString(testApk), testApkLocal)
            else assert(testApk, testApkGcs)

            if (local) assert(getString(appApk), appApkLocal)
            else assert(appApk, appApkGcs)

            assert(autoGoogleLogin, true)
            assert(useOrchestrator, true)
            assert(environmentVariables, mapOf(Pair("clearPackageData", "true")))
            assert(directoriesToPull, listOf(directoryToPull))
            assert(resultsBucket, "mockBucket")
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
        }

        with(config) {
            assert(testShards, 1)
            assert(testRuns, 1)
        }
    }

    private fun configWithTestMethods(amount: Int, testShards: Int = 1): AndroidArgs {
        val testMethods = mutableListOf<String>()
        // test names must be unique otherwise the Set<String> will add them only once.
        repeat(amount) { index -> testMethods.add(testName + index) }

        return AndroidArgs(
            GcloudYml(GcloudYmlParams()),
            AndroidGcloudYml(
                AndroidGcloudYmlParams(
                    app = appApkLocal,
                    test = testApkLocal,
                    testTargets = testMethods
                )
            ),
            FlankYml(
                FlankYmlParams(
                    testShards = testShards
                )
            )
        )
    }

    @Test
    fun calculateShards() {
        var config = configWithTestMethods(1)
        with(config) {
            assert(testShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 1)
        }

        config = configWithTestMethods(155)
        with(config) {
            assert(testShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 155)
        }

        config = configWithTestMethods(155, testShards = 40)
        with(config) {
            assert(testShards, 40)
            assert(testShardChunks.size, 39)
            assert(testShardChunks.first().size, 4)
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
        if (bitrise) return

        val oldConfig = AndroidArgs.load(localYamlFile)
        // Need to set the project id to get the bucket info from StorageOptions
        val config = AndroidArgs(
            GcloudYml(
                GcloudYmlParams(
                    resultsBucket = oldConfig.resultsBucket,
                    project = "delta-essence-114723"
                )
            ),
            AndroidGcloudYml(
                AndroidGcloudYmlParams(
                    app = oldConfig.appApk,
                    test = oldConfig.testApk
                )
            ),
            FlankYml()
        )

        assert(config.resultsBucket, "mockBucket")
    }
}
