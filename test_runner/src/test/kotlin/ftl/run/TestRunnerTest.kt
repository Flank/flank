package ftl.run

import com.google.common.truth.Truth.assertThat
import com.google.testing.Testing
import com.google.testing.model.GoogleCloudStorage
import com.google.testing.model.ResultStorage
import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import flank.common.isWindows
import ftl.adapter.google.getFilePathToDownload
import ftl.api.Artifacts.DownloadPath
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.client.google.getAndroidAppDetails
import ftl.http.executeWithRetry
import ftl.test.util.FlankTestRunner
import ftl.test.util.LocalGcs
import ftl.util.getMockedTestMatrix
import ftl.util.mockTestMatrices
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import java.nio.file.Paths

private const val OBJECT_NAME = "2019-03-22_15-30-02.189000_frjt"
private const val FILE_NAME = "StandardOutputAndStandardError.txt"
private const val SHARD = "shard_0"
private const val MATRIX = "matrix_1"
private const val ANDROID_DEVICE = "NexusLowRes-27-en-portrait-shard_0-rerun_1"
private const val IOS_DEVICE = "iphone8-12.0-en-portrait"
private const val LOCAL_DIR = "results"
private const val TEST_FILE_PATH = "any/path/that/is/possible"
private const val FULL_IOS_PATH = "$OBJECT_NAME/$SHARD/$IOS_DEVICE/$TEST_FILE_PATH/$FILE_NAME"
private const val FULL_ANDROID_PATH = "$OBJECT_NAME/$MATRIX/$ANDROID_DEVICE/$TEST_FILE_PATH/$FILE_NAME"

@RunWith(FlankTestRunner::class)
class TestRunnerTest {

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath false - ios`() {
        val downloadPath = DownloadPath(LOCAL_DIR, false, false)

        val downloadFile = getFilePathToDownload(downloadPath, FULL_IOS_PATH)
        assertThat(downloadFile).isEqualTo(Paths.get("$LOCAL_DIR/$OBJECT_NAME/$SHARD/$IOS_DEVICE/$FILE_NAME"))
    }

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath true - ios`() {
        val downloadPath = DownloadPath(LOCAL_DIR, false, true)

        val downloadFile = getFilePathToDownload(downloadPath, FULL_IOS_PATH)
        assertThat(downloadFile).isEqualTo(Paths.get("$LOCAL_DIR/$FULL_IOS_PATH"))
    }

    @Test
    fun `Verify getDownloadPath localResultDir true and keepFilePath false - ios`() {
        val downloadPath = DownloadPath(LOCAL_DIR, true, false)

        val downloadFile = getFilePathToDownload(downloadPath, FULL_IOS_PATH)
        assertThat(downloadFile).isEqualTo(Paths.get("$LOCAL_DIR/$SHARD/$IOS_DEVICE/$FILE_NAME"))
    }

    @Test
    fun `Verify getDownloadPath localResultDir true and keepFilePath true - ios`() {
        val downloadPath = DownloadPath(LOCAL_DIR, true, true)

        val downloadFile = getFilePathToDownload(downloadPath, FULL_IOS_PATH)
        assertThat(downloadFile).isEqualTo(Paths.get("$LOCAL_DIR/$SHARD/$IOS_DEVICE/$TEST_FILE_PATH/$FILE_NAME"))
    }

    @Test
    fun `Verify getDownloadPath localResultDir true and keepFilePath true - android`() {
        val downloadPath = DownloadPath(LOCAL_DIR, true, true)

        val downloadFile = getFilePathToDownload(downloadPath, FULL_ANDROID_PATH)
        assertThat(downloadFile).isEqualTo(Paths.get("$LOCAL_DIR/$MATRIX/$ANDROID_DEVICE/$TEST_FILE_PATH/$FILE_NAME"))
    }

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath true`() {
        val downloadPath = DownloadPath(LOCAL_DIR, false, true)

        val downloadFile = getFilePathToDownload(downloadPath, FULL_ANDROID_PATH)
        assertThat(downloadFile).isEqualTo(Paths.get("$LOCAL_DIR/$FULL_ANDROID_PATH"))
    }

    @Test
    fun `mockedAndroidTestRun local`() {
        val localConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        runBlocking {
            localConfig.newTestRun()
        }
    }

    @Test
    fun `mockedAndroidTestRun gcsAndHistoryName`() {
        // given
        LocalGcs.uploadFileForTest("../test_projects/android/apks/app-debug.apk")
        LocalGcs.uploadFileForTest("../test_projects/android/apks/app-debug-androidTest.apk")

        // then
        val gcsConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.gcs.yml"))
        runBlocking {
            gcsConfig.newTestRun()
        }
    }

    @Test
    fun `mockedIosTestRun local`() {
        assumeFalse(isWindows)

        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.yml"))
        runBlocking {
            config.newTestRun()
        }
    }

    @Test
    fun `mockedIosTestRun gcsAndHistoryName`() {
        assumeFalse(isWindows)

        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.gcs.yml"))
        runBlocking {
            config.newTestRun()
        }
    }

    @Test
    fun `matrix webLink should be printed before polling matrices`() {
        val localConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        runBlocking {
            localConfig.newTestRun()
        }
        val matrixWebLinkHeader = "Matrices webLink"
        val matrixLink = Regex("(matrix-\\d+ https://console\\.firebase\\.google\\.com/project/.*/testlab/histories/.*/matrices/.*)(/executions/.*)?")
        val output = systemOutRule.log
        assertTrue(output.contains(matrixWebLinkHeader))
        assertTrue(output.contains(matrixLink))
    }

    @Test
    fun `flank should stop updating web link if matrix has invalid state`() {
        val localConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        mockTestMatrices(
            getMockedTestMatrix().apply { state = "RUNNING" },
            getMockedTestMatrix().apply { state = "RUNNING" },
            getMockedTestMatrix()
        )
        mockkStatic("ftl.client.google.AppDetailsKt")
        every {
            getAndroidAppDetails(any())
        } returns ""

        runBlocking {
            localConfig.newTestRun()
        }
        val matrixWebLinkHeader = "Matrices webLink"
        val message = "Unable to get web link"
        val matrixLink = Regex("(matrix-\\d+ https://console\\.firebase\\.google\\.com/project/.*/testlab/histories/.*/matrices/.*)(/executions/.*)?")
        val output = systemOutRule.log
        assertTrue(output.contains(matrixWebLinkHeader))
        assertTrue(output.contains(message))
        assertFalse(output.contains(matrixLink))
        verify(exactly = 3) { any<Testing.Projects.TestMatrices.Get>().executeWithRetry() }
    }

}