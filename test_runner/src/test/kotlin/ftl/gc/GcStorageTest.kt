package ftl.gc

import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
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

        GcStorage.upload(androidArgs.appApk!!, "does-not-exist", "nope")
    }

    @Test
    fun `fix duplicated file names`() {
        Assert.assertEquals(
            listOf(
                "gs://bucket/gcsPath/foo",
                "gs://bucket/gcsPath/bar",
                "gs://bucket/gcsPath/bar_1",
                "gs://bucket/gcsPath/bar_2",
                "gs://bucket/gcsPath/baz.foo",
                "gs://bucket/gcsPath/baz_1.foo",
                "gs://bucket/gcsPath/baz_2.foo"
            ),
            listOf(
                "path/foo",
                "path/bar",
                "path1/bar",
                "path2/bar",
                "path/baz.foo",
                "path1/baz.foo",
                "path2/baz.foo"
            ).map { filePath ->
                GcStorage.upload(
                    filePath = filePath,
                    fileBytes = ByteArray(0),
                    rootGcsBucket = "bucket",
                    runGcsPath = "gcsPath",
                    storage = mockk(relaxed = true)
                )
            }
        )
    }
}
