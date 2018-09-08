package ftl.gc

import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GcStorageTest {

    @Test
    fun upload_missingBucket() {
        val androidArgs = mock(AndroidArgs::class.java)
        `when`(androidArgs.appApk).thenReturn("../test_app/apks/app-debug.apk")
        GcStorage.uploadAppApk(androidArgs, "does-not-exist", "nope")
    }
}
