package ftl.run

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants.isWindows
import ftl.test.util.FlankTestRunner
import ftl.util.ObjPath
import java.nio.file.Paths
import kotlinx.coroutines.runBlocking
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class TestRunnerTest {

    private val gcsPath =
        "2019-03-22_15-30-02.189000_frjt/shard_0/iphone8-12.0-en-portrait/TestLogs/Test-Transient Testing-2019.03.22_08-29-41--0700.xcresult/1_Test/Diagnostics/EarlGreyExampleSwiftTests-C6803D8C-4BDB-4C84-8945-9AC64056FBA4/EarlGreyExampleSwiftTests-EDBFF942-A88A-46A5-87CA-A1E29555C2CA/StandardOutputAndStandardError.txt"
    private val localResultDir = "results"
    private val args = mock(AndroidArgs::class.java)

    @Test
    fun `Verify getDownloadPath localResultDir false and keepFilePath false`() {
        val parsed = ObjPath.parse(gcsPath)

        `when`(args.localResultDir).thenReturn(localResultDir)
        `when`(args.useLocalResultDir()).thenReturn(false)

        val downloadFile = TestRunner.getDownloadPath(args, gcsPath)
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
        val parsed = ObjPath.parse(gcsPath)

        `when`(args.localResultDir).thenReturn(localResultDir)
        `when`(args.useLocalResultDir()).thenReturn(true)

        val downloadFile = TestRunner.getDownloadPath(args, gcsPath)
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
        val parsed = ObjPath.parse(gcsPath)

        `when`(args.localResultDir).thenReturn(localResultDir)
        `when`(args.useLocalResultDir()).thenReturn(true)
        `when`(args.keepFilePath).thenReturn(true)

        val downloadFile = TestRunner.getDownloadPath(args, gcsPath)
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
        val parsed = ObjPath.parse(gcsPath)

        `when`(args.localResultDir).thenReturn(localResultDir)
        `when`(args.useLocalResultDir()).thenReturn(false)
        `when`(args.keepFilePath).thenReturn(true)

        val downloadFile = TestRunner.getDownloadPath(args, gcsPath)
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
            TestRunner.newRun(localConfig)
        }
    }

    @Test
    fun `mockedAndroidTestRun gcsAndHistoryName`() {
        val gcsConfig = AndroidArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.gcs.yml"))
        runBlocking {
            TestRunner.newRun(gcsConfig)
        }
    }

    @Test
    fun `mockedIosTestRun local`() {
        assumeFalse(isWindows)

        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.yml"))
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    @Test
    fun `mockedIosTestRun gcsAndHistoryName`() {
        assumeFalse(isWindows)

        val config = IosArgs.load(Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.gcs.yml"))
        runBlocking {
            TestRunner.newRun(config)
        }
    }
}
