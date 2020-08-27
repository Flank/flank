package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.AppTestPair
import ftl.config.Device
import ftl.config.defaultAndroidConfig
import ftl.run.platform.android.createAndroidTestContexts
import ftl.run.platform.android.getAndroidMatrixShards
import ftl.run.status.OutputStyle
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import ftl.test.util.TestHelper.getString
import ftl.run.exception.FlankGeneralError
import kotlinx.coroutines.runBlocking
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

    private val ymlNotFound = getPath("not_found.yml")
    private val localYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.local.yml")
    private val gcsYamlFile = getPath("src/test/kotlin/ftl/fixtures/flank.gcs.yml")
    private val appApkLocal = getString("../test_projects/android/apks/app-debug.apk")
    private val appApkGcs = "gs://tmp_bucket_2/app-debug.apk"
    private val testApkLocal = getString("../test_projects/android/apks/app-debug-androidTest.apk")
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

            if (local) assert(getString(appApk!!), getString(appApkAbsolutePath))
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
                    Device("NexusLowRes", "23", "en", "portrait", isVirtual = true),
                    Device("NexusLowRes", "23", "en", "landscape", isVirtual = true),
                    Device("shamu", "22", "zh_CN", "default", isVirtual = false)
                )
            )
            assert(maxTestShards, 1)
            assert(repeatTests, 1)
        }
    }

    private fun configWithTestMethods(
        amount: Int,
        maxTestShards: Int = 1
    ): AndroidArgs = createAndroidArgs(
        defaultAndroidConfig().apply {
            platform.apply {
                gcloud.apply {
                    app = appApkLocal
                    test = getString("src/test/kotlin/ftl/fixtures/tmp/apk/app-debug-androidTest_$amount.apk")
                }
            }
            common.flank.maxTestShards = maxTestShards
        }
    )

    @Test
    fun `calculateShards additionalAppTestApks`() {
        val test1 = "src/test/kotlin/ftl/fixtures/tmp/apk/app-debug-androidTest_1.apk"
        val test155 = "src/test/kotlin/ftl/fixtures/tmp/apk/app-debug-androidTest_155.apk"
        val config = createAndroidArgs(
            defaultAndroidConfig().apply {
                platform.apply {
                    gcloud.apply {
                        app = appApkLocal
                        test = getString(test1)
                    }
                    flank.apply {
                        additionalAppTestApks = mutableListOf(
                            AppTestPair(
                                app = appApkLocal,
                                test = getString(test155)
                            )
                        )
                    }
                }
                common.flank.maxTestShards = 3
            }
        )
        with(runBlocking { config.getAndroidMatrixShards() }) {
            assertEquals(1, get("matrix-0")!!.shards["shard-0"]!!.size)
            assertEquals(51, get("matrix-1")!!.shards["shard-0"]!!.size)
            assertEquals(52, get("matrix-1")!!.shards["shard-1"]!!.size)
            assertEquals(52, get("matrix-1")!!.shards["shard-2"]!!.size)
        }
    }

    @Test
    fun `calculateShards 0`() = runBlocking {
        val config = configWithTestMethods(0)
        val testShardChunks = config.createAndroidTestContexts()
        with(config) {
            assert(maxTestShards, 1)
            assert(testShardChunks.size, 0)
        }
    }

    @Test
    fun `calculateShards 1`() {
        val config = configWithTestMethods(1)
        val testShardChunks = getAndroidShardChunks(config)
        with(config) {
            assert(maxTestShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 1)
        }
    }

    @Test
    fun `calculateShards 155`() {
        val config = configWithTestMethods(155)
        val testShardChunks = getAndroidShardChunks(config)
        with(config) {
            assert(maxTestShards, 1)
            assert(testShardChunks.size, 1)
            assert(testShardChunks.first().size, 155)
        }
    }

    @Test
    fun `calculateShards 155 40`() {
        val config = configWithTestMethods(155, maxTestShards = 40)
        val testShardChunks = getAndroidShardChunks(config)
        with(config) {
            assert(maxTestShards, 40)
            assert(testShardChunks.size, 40)
            assert(testShardChunks.first().size, 3)
        }
    }

    @Test
    fun `should distribute equally to shards`() {
        val config = configWithTestMethods(155, maxTestShards = 40)
        val testShardChunks = getAndroidShardChunks(config)
        with(config) {
            assert(maxTestShards, 40)
            assert(testShardChunks.size, 40)
            testShardChunks.forEach { assertThat(it.size).isIn(3..4) }
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
        val config = createAndroidArgs(
            defaultAndroidConfig().apply {
                common.apply {
                    gcloud.resultsBucket = oldConfig.resultsBucket
                    flank.project = "flank-open-source"
                }
                platform.gcloud.apply {
                    app = oldConfig.appApk
                    test = oldConfig.testApk
                }
            }
        )
        assert(config.resultsBucket, "tmp_bucket_2")
    }

    @Test
    fun `verify run timeout value from yml file`() {
        val args = AndroidArgs.load(localYamlFile)
        assertEquals(60 * 60 * 1000L, args.parsedTimeout)
    }

    @Test
    fun `verify output style value from uml file`() {
        val args = AndroidArgs.load(localYamlFile)
        assertEquals(OutputStyle.Single, args.outputStyle)
    }

    @Test(expected = FlankGeneralError::class)
    fun `should throw if load and yamlFile not found`() {
        AndroidArgs.load(ymlNotFound)
    }

    @Test
    fun `should not throw if loadOrDefault and yamlFile not found`() {
        AndroidArgs.loadOrDefault(ymlNotFound)
    }
}
