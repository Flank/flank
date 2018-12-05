package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.createGcsBucket
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosGcloudYml
import ftl.shard.TestMethod
import ftl.shard.TestShard
import ftl.shard.stringShards
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
class ArgsHelperTest {

    @Rule
    @JvmField
    val exceptionRule = ExpectedException.none()!!

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @get:Rule
    val environmentVariables = EnvironmentVariables()

    @Test
    fun mergeYmlMaps_succeeds() {
        val merged = mergeYmlMaps(GcloudYml, IosGcloudYml)
        assertThat(merged.keys.size).isEqualTo(1)
        assertThat(merged["gcloud"]?.size).isEqualTo(11)
    }

    @Test
    fun assertFileExists_succeeds() {
        assertFileExists("/tmp", "temp folder")
    }

    @Test
    fun assertFileExists_fails() {
        exceptionRule.expectMessage("'/tmp/1/2/3/fake'  doesn't exist")
        assertFileExists("/tmp/1/2/3/fake", "")
    }

    @Test
    fun assertGcsFileExists_succeeds() {
        assertGcsFileExists("gs://tmp_bucket_2/app-debug.apk")
    }

    @Test
    fun assertGcsFileExists_fails() {
        exceptionRule.expectMessage("The file at 'gs://does-not-exist' does not exist")
        assertGcsFileExists("gs://does-not-exist")
    }

    @Test(expected = IllegalArgumentException::class)
    fun assertGcsFileExists_failsOnMissingPrefix() {
        assertGcsFileExists("does-not-exist")
    }

    @Test
    fun validateTestMethods_succeeds() {
        val testTargets = listOf("a")
        val validTestMethods = listOf("a", "b", "c")
        val from = "Test APK"
        validateTestMethods(testTargets, validTestMethods, from)
    }

    @Test
    fun validateTestMethods_validationOffWhenUseMock() {
        val testTargets = listOf("d")
        val validTestMethods = listOf("a", "b", "c")
        validateTestMethods(testTargets, validTestMethods, "")
    }

    @Test
    fun validateTestMethods_validationOn() {
        exceptionRule.expectMessage(" is missing methods: [d].")
        val testTargets = listOf("d")
        val validTestMethods = listOf("a", "b", "c")
        val skipValidation = false
        validateTestMethods(testTargets, validTestMethods, "", skipValidation)
    }

    @Test
    fun validateTestMethods_validationOn_Empty() {
        exceptionRule.expectMessage("has no tests")
        val testTargets = emptyList<String>()
        val validTestMethods = emptyList<String>()
        val skipValidation = false
        validateTestMethods(testTargets, validTestMethods, "", skipValidation)
    }

    @Test
    fun yamlMapper_exists() {
        assertThat(ArgsHelper.yamlMapper).isNotNull()
    }

    @Test
    fun createGcsBucket_succeeds() {
        createGcsBucket("123", "results_bucket")
    }

    @Test
    fun getDefaultProjectId_succeeds() {
        assertThat(ArgsHelper.getDefaultProjectId())
            .isEqualTo("mockProjectId")
    }

    @Test
    fun evaluateBlobInFilePath() {
        val testApkRelativePath = "../test_app/apks/app-debug-androidTest.apk"
        val testApkBlobPath = "../test_app/**/app-debug-*.apk"

        val actual = ArgsHelper.evaluateFilePath(testApkBlobPath)
        val expected = testApkRelativePath.absolutePath()

        assertThat(actual).isEqualTo(expected)
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

    @Test(expected = java.nio.file.NoSuchFileException::class)
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
}
