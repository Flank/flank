package ftl.gc

import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class GcStorageTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `upload missingBucket`() {
        val androidArgs = mockk<AndroidArgs>()
        every { androidArgs.appApk } returns "../test_app/apks/app-debug.apk"

        GcStorage.upload(androidArgs.appApk, "does-not-exist", "nope")
    }
}
