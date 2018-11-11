package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.calculateShards
import ftl.args.ArgsHelper.getGcsBucket
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosGcloudYml
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

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
        assertThat(merged["gcloud"]?.size).isEqualTo(10)
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
    fun calculateShards_fails_emptyShardChunks() {
        exceptionRule.expectMessage("Failed to populate test shard chunks")
        calculateShards(
            testMethodsToShard = listOf(""),
            testMethodsAlwaysRun = listOf(""),
            testShards = 1
        )
    }

    @Test
    fun calculateShards_succeeds() {
        calculateShards(
            testMethodsToShard = listOf("a", "b", "c"),
            testMethodsAlwaysRun = listOf("c"),
            testShards = -1
        )
    }

    @Test
    fun calculateShards_emptyTestTargets() {
        val tests = listOf(
            "class com.example.profile.ProfileTest#testOne",
            "class com.example.profile.ProfileTest#testTwo"
        )
        val shards = calculateShards(
            testMethodsToShard = tests,
            testMethodsAlwaysRun = emptyList(),
            testShards = -1
        )
        val expectedShards = listOf(
            listOf(tests[0]),
            listOf(tests[1])
        )
        assertThat(shards).isEqualTo(expectedShards)
    }

    @Test
    fun calculateShards_packageTarget() {
        val shards = calculateShards(
            testMethodsToShard = listOf("a", "b", "c"),
            testMethodsAlwaysRun = listOf("c"),
            testShards = 2
        )

        val expectedShards = listOf(
            listOf("c", "a"),
            listOf("c", "b")
        )
        assertThat(shards).isEqualTo(expectedShards)
    }

    @Test
    fun yamlMapper_exists() {
        assertThat(ArgsHelper.yamlMapper).isNotNull()
    }

    @Test
    fun getGcsBucket_succeeds() {
        getGcsBucket("123", "results_bucket")
    }

    @Test
    fun getDefaultProjectId_succeeds() {
        assertThat(ArgsHelper.getDefaultProjectId())
            .isEqualTo("mockProjectId")
    }

    @Test
    fun evaluateBlobInFilePath() {
        val testApkBlobPath = "../test_app/**/app-debug-*.apk"
        val actual = ArgsHelper.evaluateFilePath(testApkBlobPath)

        val testApkRelativePath = "../test_app/apks/app-debug-androidTest.apk"
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

    private fun String.absolutePath(): String {
        return Paths.get(this).toAbsolutePath().normalize().toString()
    }

    @Test
    fun tmpTest() {
        Files.walkFileTree(Paths.get("/tmp"), object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                // hits '/tmp' once and doesn't iterate through the files
                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
                return FileVisitResult.CONTINUE
            }

            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                return FileVisitResult.CONTINUE
            }
        })
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
}
