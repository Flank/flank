package ftl.gc

import com.dd.plist.NSDictionary
import com.google.api.services.testing.model.IosDeviceList
import ftl.shard.Chunk
import ftl.args.IosArgs
import ftl.config.FtlConstants.isWindows
import ftl.ios.FIXTURES_PATH
import ftl.shard.TestMethod
import ftl.test.util.FlankTestRunner
import ftl.util.ShardCounter
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.StringReader

@RunWith(FlankTestRunner::class)
class GcIosTestMatrixTest {

    @After
    fun tearDown() = unmockkAll()

    @Test(expected = IllegalArgumentException::class)
    fun `build negativeShardErrors`() {
        val iosArgs = mockk<IosArgs>(relaxed = true)

        every { iosArgs.testShardChunks } returns listOf(Chunk(listOf(TestMethod(name = "", time = 0.0))))

        GcIosTestMatrix.build(
            iosDeviceList = IosDeviceList(),
            testZipGcsPath = "",
            runGcsPath = "",
            xcTestParsed = NSDictionary(),
            args = iosArgs,
            testTargets = emptyList(),
            shardCounter = ShardCounter(),
            toolResultsHistory = GcToolResults.createToolResultsHistory(iosArgs),
            otherFiles = mapOf(),
            additionalIpasGcsPaths = emptyList()
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `build invalidShardErrors`() {
        val iosArgs = mockk<IosArgs>(relaxed = true)
        GcIosTestMatrix.build(
            iosDeviceList = IosDeviceList(),
            testZipGcsPath = "",
            runGcsPath = "",
            xcTestParsed = NSDictionary(),
            args = iosArgs,
            testTargets = listOf(""),
            shardCounter = ShardCounter(),
            toolResultsHistory = GcToolResults.createToolResultsHistory(iosArgs),
            otherFiles = mapOf(),
            additionalIpasGcsPaths = emptyList()
        )
    }

    @Test
    fun `build validArgs`() {
        assumeFalse(isWindows) // TODO enable it on #1180

        val iosArgs = mockk<IosArgs>(relaxed = true)
        every { iosArgs.testShardChunks } returns listOf(Chunk(listOf(TestMethod(name = "", time = 0.0))))
        every { iosArgs.testTimeout } returns "3m"
        every { iosArgs.resultsBucket } returns "/hi"
        every { iosArgs.project } returns "123"
        every { iosArgs.xctestrunFile } returns "$FIXTURES_PATH/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun"

        GcIosTestMatrix.build(
            iosDeviceList = IosDeviceList(),
            testZipGcsPath = "",
            runGcsPath = "",
            xcTestParsed = NSDictionary(),
            args = iosArgs,
            testTargets = emptyList(),
            shardCounter = ShardCounter(),
            toolResultsHistory = GcToolResults.createToolResultsHistory(iosArgs),
            otherFiles = mapOf(),
            additionalIpasGcsPaths = emptyList()
        )
    }


    @Test
    fun `should fill otherFiles`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun
              results-dir: test_dir
              other-files:
                com.my.app:/Documents/file.txt: local/file.txt
                /private/var/mobile/Media/file.jpg: gs://bucket/file.jpg
        """.trimIndent()
            )
        )

        val expected = mapOf(
            "com.my.app:/Documents/file.txt" to "local/file.txt",
            "/private/var/mobile/Media/file.jpg" to "gs://bucket/file.jpg"
        )
        assertEquals(expected, iosArgs.otherFiles)
    }

    @Test
    fun `should not fill otherFiles`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun
              results-dir: test_dir
        """.trimIndent()
            )
        )

        val expected = emptyMap<String, String>()
        assertEquals(expected, iosArgs.otherFiles)
    }

    @Test
    fun `should fill additional ipas`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun
              results-dir: test_dir
              additional-ipas:
                - path/to/local/file.ipa
                - gs://bucket/file.ipa
        """.trimIndent()
            )
        )

        val expected = listOf("path/to/local/file.ipa", "gs://bucket/file.ipa")
        assertEquals(expected, iosArgs.additionalIpas)
    }


    @Test
    fun `should not fill additional ipas`() {
        val iosArgs = IosArgs.load(
            StringReader(
                """
            gcloud:
              test: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip
              xctestrun-file: ./test_runner/src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun
              results-dir: test_dir
        """.trimIndent()
            )
        )

        val expected = emptyList<String>()
        assertEquals(expected, iosArgs.additionalIpas)
    }
}
