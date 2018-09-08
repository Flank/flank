package ftl.gc

import com.google.api.services.testing.model.AndroidDeviceList
import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GcAndroidTestMatrixTest {

    @Test(expected = IllegalArgumentException::class)
    fun build_negativeShardErrors() {
        val androidArgs = mock(AndroidArgs::class.java)
        GcAndroidTestMatrix.build(
            "", "", "",
            AndroidDeviceList(), -2, androidArgs
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun build_invalidShardErrors() {
        val androidArgs = mock(AndroidArgs::class.java)
        GcAndroidTestMatrix.build(
            "", "", "",
            AndroidDeviceList(), 1, androidArgs
        )
    }

    @Test
    fun build_validArgs() {
        val androidArgs = mock(AndroidArgs::class.java)
        `when`(androidArgs.testShardChunks).thenReturn(listOf(listOf("")))
        `when`(androidArgs.testTimeout).thenReturn("3m")
        `when`(androidArgs.resultsBucket).thenReturn("/hi")
        `when`(androidArgs.projectId).thenReturn("123")

        GcAndroidTestMatrix.build(
            "", "", "",
            AndroidDeviceList(), 0, androidArgs
        )
    }
}
