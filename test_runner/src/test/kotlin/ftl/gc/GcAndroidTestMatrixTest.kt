package ftl.gc

import com.google.api.services.testing.model.AndroidDeviceList
import ftl.args.AndroidArgs
import ftl.gc.GcToolResults.createToolResultsHistory
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class GcAndroidTestMatrixTest {

    @After
    fun tearDown() = unmockkAll()

    @Test(expected = IllegalArgumentException::class)
    fun `build negativeShardErrors`() {
        val androidArgs = mockk<AndroidArgs>(relaxed = true)

        GcAndroidTestMatrix.build(
            appApkGcsPath = "",
            testApkGcsPath = "",
            runGcsPath = "",
            otherFiles = emptyMap(),
            androidDeviceList = AndroidDeviceList(),
            testShards = emptyList(),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs),
            additionalApkGcsPaths = emptyList(),
            roboScriptGcsPath = null
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `build invalidShardErrors`() {
        val androidArgs = mockk<AndroidArgs>(relaxed = true)

        GcAndroidTestMatrix.build(
            appApkGcsPath = "",
            testApkGcsPath = "",
            runGcsPath = "",
            otherFiles = emptyMap(),
            androidDeviceList = AndroidDeviceList(),
            testShards = listOf(listOf("")),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs),
            additionalApkGcsPaths = emptyList(),
            roboScriptGcsPath = null
        )
    }

    @Test
    fun `build validArgs`() {
        val androidArgs = mockk<AndroidArgs>(relaxed = true)

        every { androidArgs.testTimeout } returns "3m"
        every { androidArgs.resultsBucket } returns "/hi"
        every { androidArgs.project } returns "123"

        GcAndroidTestMatrix.build(
            appApkGcsPath = "",
            testApkGcsPath = "",
            runGcsPath = "",
            otherFiles = emptyMap(),
            androidDeviceList = AndroidDeviceList(),
            testShards = emptyList(),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs),
            additionalApkGcsPaths = emptyList(),
            roboScriptGcsPath = null
        )
    }
}
