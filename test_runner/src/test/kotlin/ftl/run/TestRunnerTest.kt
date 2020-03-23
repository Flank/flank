package ftl.run

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants.isWindows
import ftl.run.common.getDownloadPath
import ftl.test.util.FlankTestRunner
import ftl.util.ObjPath
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assume.assumeFalse
import org.junit.Test
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

        @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath false`() {
        val parsed = ObjPath.legacyParse(gcsIosPath)

        every { iosArgs.localResultDir } returns localResultDir
        every { iosArgs.useLocalResultDir() } returns false

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
    fun `Verify getDownloadPath localResultDir true and keepFilePath false`() {
        val parsed = ObjPath.legacyParse(gcsIosPath)

        every { iosArgs.localResultDir } returns localResultDir
        every { iosArgs.useLocalResultDir() } returns true

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
    fun `Verify getDownloadPath localResultDir true and keepFilePath true`() {
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
}
