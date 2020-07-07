package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.ArgsHelper.assertCommonProps
import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.createGcsBucket
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.yml.mergeYmlKeys
import ftl.config.FtlConstants
import ftl.config.common.CommonGcloudConfig
import ftl.config.ios.IosGcloudConfig
import ftl.shard.TestMethod
import ftl.shard.TestShard
import ftl.shard.stringShards
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.assertThrowsWithMessage
import ftl.util.FlankFatalError
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assume
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ArgsHelperTest {

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @get:Rule
    val environmentVariables = EnvironmentVariables()

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `mergeYmlMaps succeeds`() {
        val merged = mergeYmlKeys(CommonGcloudConfig, IosGcloudConfig)
        assertThat(merged.keys.size).isEqualTo(1)
        assertThat(merged["gcloud"]?.size).isEqualTo(11)
    }

    @Test
    fun `assertFileExists succeeds`() {
        assertFileExists("/tmp", "temp folder")
    }

    @Test
    fun `assertFileExists fails`() {
        assertThrowsWithMessage(Throwable::class, "'/tmp/1/2/3/fake'  doesn't exist") {
            assertFileExists("/tmp/1/2/3/fake", "")
        }
    }

    @Test
    fun `assertGcsFileExists succeeds`() {
        assertGcsFileExists("gs://tmp_bucket_2/app-debug.apk")
    }

    @Test
    fun `assertGcsFileExists fails`() {
        assertThrowsWithMessage(Throwable::class, "The file at 'gs://does-not-exist' does not exist") {
            assertGcsFileExists("gs://does-not-exist")
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `assertGcsFileExists failsOnMissingPrefix`() {
        assertGcsFileExists("does-not-exist")
    }

    @Test
    fun `validateTestMethods succeeds`() {
        val testTargets = listOf("a")
        val validTestMethods = listOf("a", "b", "c")
        val from = "Test APK"
        validateTestMethods(testTargets, validTestMethods, from)
    }

    @Test
    fun `validateTestMethods validationOffWhenUseMock`() {
        val testTargets = listOf("d")
        val validTestMethods = listOf("a", "b", "c")
        validateTestMethods(testTargets, validTestMethods, "")
    }

    @Test
    fun `validateTestMethods validationOn`() {
        val testTargets = listOf("d")
        val validTestMethods = listOf("a", "b", "c")
        val skipValidation = false
        assertThrowsWithMessage(Throwable::class, " is missing methods: [d].") {
            validateTestMethods(testTargets, validTestMethods, "", skipValidation)
        }
    }

    @Test
    fun `validateTestMethods validationOn Empty`() {
        val testTargets = emptyList<String>()
        val validTestMethods = emptyList<String>()
        val skipValidation = false
        assertThrowsWithMessage(Throwable::class, "has no tests") {
            validateTestMethods(testTargets, validTestMethods, "", skipValidation)
        }
    }

    @Test
    fun `yamlMapper exists`() {
        assertThat(ArgsHelper.yamlMapper).isNotNull()
    }

    @Test
    fun `createGcsBucket succeeds`() {
        createGcsBucket("123", "results_bucket")
    }

    @Test
    fun `getDefaultProjectId succeeds`() {
        assertThat(ArgsHelper.getDefaultProjectId())
            .isEqualTo("mockProjectId")
    }

    private fun makeTmpFile(filePath: String): String {
        val file = File(filePath)
        file.parentFile.mkdirs()

        file.apply {
            createNewFile()
            deleteOnExit()
        }

        return file.absolutePath
    }

    @Test
    fun evaluateTildeInFilePath() {
        Assume.assumeFalse(FtlConstants.isWindows)

        val expected = makeTmpFile("/tmp/random.xctestrun")

        val inputPath = "~/../../tmp/random.xctestrun"
        val actual = ArgsHelper.evaluateFilePath(inputPath)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun evaluateEnvVarInFilePath() {
        environmentVariables.set("TEST_APK_DIR", "test_app/apks")
        val testApkPath = "../\$TEST_APK_DIR/app-debug-androidTest.apk"
        val actual = ArgsHelper.evaluateFilePath(testApkPath)
        val expected = "../test_app/apks/app-debug-androidTest.apk".absolutePath()

        assertThat(actual).isEqualTo(expected)
    }

    @Test(expected = FlankFatalError::class)
    fun evaluateInvalidFilePath() {
        val testApkPath = "~/flank_test_app/invalid_path/app-debug-*.xctestrun"
        ArgsHelper.evaluateFilePath(testApkPath)
    }

    @Test
    fun stringShardsTest() {
        val shards = listOf(
            TestShard(3.0, mutableListOf(TestMethod("a", 1.0), TestMethod("b", 2.0))),
            TestShard(4.0, mutableListOf(TestMethod("c", 4.0))),
            TestShard(5.0, mutableListOf(TestMethod("d", 2.0), TestMethod("e", 3.0)))
        )

        val expected = listOf(
            listOf("a", "b"),
            listOf("c"),
            listOf("d", "e")
        )

        assertThat(shards.stringShards()).isEqualTo(expected)
    }

    @Test
    fun testInvalidTestShards() {
        val maxTestShards = -2

        val args = spyk(AndroidArgs.default())
        every { args.maxTestShards } returns maxTestShards
        assertThrowsWithMessage(Throwable::class, "max-test-shards must be >= ${IArgs.AVAILABLE_SHARD_COUNT_RANGE.first} and <= ${IArgs.AVAILABLE_SHARD_COUNT_RANGE.last}. But current is $maxTestShards") {
            assertCommonProps(args)
        }
    }

    @Test
    fun testInvalidShardTime() {
        val args = spyk(AndroidArgs.default())
        every { args.shardTime } returns -2
        assertThrowsWithMessage(Throwable::class, "shard-time must be >= 1 or -1") {
            assertCommonProps(args)
        }
    }

    @Test
    fun testInvalidRepeatTests() {
        val args = spyk(AndroidArgs.default())
        every { args.repeatTests } returns 0
        assertThrowsWithMessage(Throwable::class, "num-test-runs must be >= 1") {
            assertCommonProps(args)
        }
    }
}
