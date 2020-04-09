package ftl.run

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.GoogleCloudStorage
import com.google.api.services.testing.model.ResultStorage
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants.isWindows
import ftl.http.executeWithRetry
import ftl.run.common.getDownloadPath
import ftl.test.util.FlankTestRunner
import ftl.util.ObjPath
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class TestRunnerTest {

    private val gcsIosPath =
        "2019-03-22_15-30-02.189000_frjt/shard_0/iphone8-12.0-en-portrait/TestLogs/Test-Transient Testing-2019.03.22_08-29-41--0700.xcresult/1_Test/Diagnostics/EarlGreyExampleSwiftTests-C6803D8C-4BDB-4C84-8945-9AC64056FBA4/EarlGreyExampleSwiftTests-EDBFF942-A88A-46A5-87CA-A1E29555C2CA/StandardOutputAndStandardError.txt"
    private val gcsAndroidPath =
        "2019-03-22_15-30-02.189000_frjt/iphone8-12.0-en-portrait-shard_0/StandardOutputAndStandardError.txt"
    private val localResultDir = "results"
    private val iosArgs = mockk<IosArgs>()
    private val androidArgs = mockk<AndroidArgs>()

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath false - ios`() {
        val parsed = ObjPath.legacyParse(gcsIosPath)

        every { iosArgs.localResultDir } returns localResultDir
        every { iosArgs.useLocalResultDir() } returns false
        every { iosArgs.keepFilePath } returns false

        val downloadFile = getDownloadPath(iosArgs, gcsIosPath)
        assertThat(downloadFile).isEqualTo(
            Paths.get(
                localResultDir,
                parsed.objName,
                parsed.shardName,
                parsed.deviceName,
                parsed.fileName
            )
        )
    }

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath true - ios`() {
        val parsed = ObjPath.legacyParse(gcsIosPath)

        every { iosArgs.localResultDir } returns localResultDir
        every { iosArgs.useLocalResultDir() } returns false
        every { iosArgs.keepFilePath } returns true

        val downloadFile = getDownloadPath(iosArgs, gcsIosPath)
        assertThat(downloadFile).isEqualTo(
            Paths.get(
                localResultDir,
                parsed.objName,
                parsed.shardName,
                parsed.deviceName,
                parsed.filePathName,
                parsed.fileName
            )
        )
    }

    @Test
    fun `Verify getDownloadPath localResultDir true and keepFilePath false - ios`() {
        val parsed = ObjPath.legacyParse(gcsIosPath)

        every { iosArgs.localResultDir } returns localResultDir
        every { iosArgs.useLocalResultDir() } returns true
        every { iosArgs.keepFilePath } returns false

        val downloadFile = getDownloadPath(iosArgs, gcsIosPath)
        assertThat(downloadFile).isEqualTo(
            Paths.get(
                localResultDir,
                parsed.shardName,
                parsed.deviceName,
                parsed.fileName
            )
        )
    }

    @Test
    fun `Verify getDownloadPath localResultDir true and keepFilePath true - ios`() {
        val parsed = ObjPath.legacyParse(gcsIosPath)

        every { iosArgs.localResultDir } returns localResultDir
        every { iosArgs.useLocalResultDir() } returns true
        every { iosArgs.keepFilePath } returns true

        val downloadFile = getDownloadPath(iosArgs, gcsIosPath)
        assertThat(downloadFile).isEqualTo(
            Paths.get(
                localResultDir,
                parsed.shardName,
                parsed.deviceName,
                parsed.filePathName,
                parsed.fileName
            )
        )
    }

    @Test
    fun `Verify getDownloadPath localResultDir true and keepFilePath true - android`() {
        val parsed = ObjPath.parse(gcsAndroidPath)

        every { androidArgs.localResultDir } returns localResultDir
        every { androidArgs.useLocalResultDir() } returns true
        every { androidArgs.keepFilePath } returns true

        val downloadFile = getDownloadPath(androidArgs, gcsAndroidPath)
        assertThat(downloadFile).isEqualTo(
            Paths.get(
                localResultDir,
                parsed.shardName,
                parsed.deviceName,
                parsed.filePathName,
                parsed.fileName
            )
        )
    }

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath true`() {
        val parsed = ObjPath.parse(gcsAndroidPath)

        every { androidArgs.localResultDir } returns localResultDir
        every { androidArgs.useLocalResultDir() } returns false
        every { androidArgs.keepFilePath } returns true

        val downloadFile = getDownloadPath(androidArgs, gcsAndroidPath)
        assertThat(downloadFile).isEqualTo(
            Paths.get(
                localResultDir,
                parsed.objName,
                parsed.shardName,
                parsed.deviceName,
                parsed.filePathName,
                parsed.fileName
            )
        )
    }

    @Test
    fun `mockedAndroidTestRun local`() {
        val localConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        runBlocking {
            newTestRun(localConfig)
        }
    }

    @Test
    fun `mockedAndroidTestRun gcsAndHistoryName`() {
        val gcsConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.gcs.yml"))
        runBlocking {
            newTestRun(gcsConfig)
        }
    }

    @Test
    fun `mockedIosTestRun local`() {
        assumeFalse(isWindows)

        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.yml"))
        runBlocking {
            newTestRun(config)
        }
    }

    @Test
    fun `mockedIosTestRun gcsAndHistoryName`() {
        assumeFalse(isWindows)

        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.gcs.yml"))
        runBlocking {
            newTestRun(config)
        }
    }

    @Test
    fun `matrix webLink should be printed before polling matrices`() {
        val localConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        runBlocking {
            newTestRun(localConfig)
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
        mockkStatic("ftl.http.ExecuteWithRetryKt")
        every {
            any<Testing.Projects.TestMatrices.Get>().executeWithRetry()
        } returnsMany listOf(
            getMockedTestMatrix().apply { state = "RUNNING" },
            getMockedTestMatrix().apply { state = "RUNNING" },
            getMockedTestMatrix()
        )
        runBlocking {
            newTestRun(localConfig)
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

    private fun getMockedTestMatrix() = TestMatrix().apply {
        state = "INVALID"
        testMatrixId = "matrix-12345"
        testExecutions = listOf(TestExecution().apply {
            resultStorage = ResultStorage().apply {
                googleCloudStorage = GoogleCloudStorage().apply {
                    gcsPath = "any/Path"
                }
            }
        })
    }
}
