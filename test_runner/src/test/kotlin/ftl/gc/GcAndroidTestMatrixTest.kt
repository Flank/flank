package ftl.gc

import com.google.api.services.testing.model.AndroidDeviceList
import ftl.args.AndroidArgs
import ftl.gc.GcToolResults.createToolResultsHistory
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GcAndroidTestMatrixTest {

    @Test(expected = IllegalArgumentException::class)
    fun `build negativeShardErrors`() {
        val androidArgs = mock(AndroidArgs::class.java)

        GcAndroidTestMatrix.build(
            appApkGcsPath = "",
            testApkGcsPath = "",
            runGcsPath = "",
            androidDeviceList = AndroidDeviceList(),
            testTargets = emptyList(),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `build invalidShardErrors`() {
        val androidArgs = mock(AndroidArgs::class.java)
        GcAndroidTestMatrix.build(
            appApkGcsPath = "",
            testApkGcsPath = "",
            runGcsPath = "",
            androidDeviceList = AndroidDeviceList(),
            testTargets = listOf(listOf("")),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs)
        )
    }

    @Test
    fun `build validArgs`() {
        val androidArgs = mock(AndroidArgs::class.java)
        `when`(androidArgs.testTimeout).thenReturn("3m")
        `when`(androidArgs.resultsBucket).thenReturn("/hi")
        `when`(androidArgs.project).thenReturn("123")

        GcAndroidTestMatrix.build(
            appApkGcsPath = "",
            testApkGcsPath = "",
            runGcsPath = "",
            androidDeviceList = AndroidDeviceList(),
            testTargets = emptyList(),
            args = androidArgs,
            toolResultsHistory = createToolResultsHistory(androidArgs)
        )
    }
}
