package ftl.gc

import com.dd.plist.NSDictionary
import com.google.api.services.testing.model.IosDeviceList
import ftl.args.Chunk
import ftl.args.IosArgs
import ftl.shard.TestMethod
import ftl.test.util.FlankTestRunner
import ftl.util.ShardCounter
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

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
            toolResultsHistory = GcToolResults.createToolResultsHistory(iosArgs)
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
            toolResultsHistory = GcToolResults.createToolResultsHistory(iosArgs)
        )
    }

    @Test
    fun `build validArgs`() {
        val iosArgs = mockk<IosArgs>(relaxed = true)
        every { iosArgs.testShardChunks } returns listOf(Chunk(listOf(TestMethod(name = "", time = 0.0))))
        every { iosArgs.testTimeout } returns "3m"
        every { iosArgs.resultsBucket } returns "/hi"
        every { iosArgs.project } returns "123"
        every { iosArgs.xctestrunFile } returns "any/path/to/test/file.xctestrun"

        GcIosTestMatrix.build(
            iosDeviceList = IosDeviceList(),
            testZipGcsPath = "",
            runGcsPath = "",
            xcTestParsed = NSDictionary(),
            args = iosArgs,
            testTargets = emptyList(),
            shardCounter = ShardCounter(),
            toolResultsHistory = GcToolResults.createToolResultsHistory(iosArgs)
        )
    }
}
