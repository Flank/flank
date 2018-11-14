package ftl.args

import com.google.common.truth.Truth
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
class ArgsHelperFilePathTest {

    @get:Rule
    val environmentVariables = EnvironmentVariables()

    @Test
    fun evaluateBlobInFilePath() {
        val testApkBlobPath = "../test_app/**/app-debug-*.apk"
        val actual = ArgsHelper.evaluateFilePath(testApkBlobPath)

        val testApkRelativePath = "../test_app/apks/app-debug-androidTest.apk"
        val expected = testApkRelativePath.absolutePath()

        Truth.assertThat(actual).isEqualTo(expected)
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

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun evaluateEnvVarInFilePath() {
        environmentVariables.set("TEST_APK_DIR", "test_app/apks")
        val testApkPath = "../\$TEST_APK_DIR/app-debug-androidTest.apk"
        val actual = ArgsHelper.evaluateFilePath(testApkPath)

        val expected = "../test_app/apks/app-debug-androidTest.apk".absolutePath()

        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test(expected = java.nio.file.NoSuchFileException::class)
    fun evaluateInvalidFilePath() {
        val testApkPath = "~/flank_test_app/invalid_path/app-debug-*.xctestrun"
        ArgsHelper.evaluateFilePath(testApkPath)
    }
}
