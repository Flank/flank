package ftl.gc

import com.google.api.services.toolresults.model.AppStartTime
import com.google.api.services.toolresults.model.Duration
import com.google.api.services.toolresults.model.PerfMetricsSummary
import ftl.adapter.google.GcStorage
import ftl.args.AndroidArgs
import ftl.test.util.FlankTestRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
class GcStorageTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `upload missingBucket`() {
        val androidArgs = mockk<AndroidArgs>()
        every { androidArgs.appApk } returns "../test_projects/android/apks/app-debug.apk"

        GcStorage.upload(androidArgs.appApk!!, "does-not-exist", "nope")
    }

    @Test
    fun `should upload performance metrics`() {
        // given
        val expectedPerformanceMetrics = PerfMetricsSummary()
            .setAppStartTime(AppStartTime().setInitialDisplayTime(Duration().setSeconds(5)))

        // when
        val filePath = GcStorage.uploadPerformanceMetrics(expectedPerformanceMetrics, "bucket", "path/test")

        // then
        assertTrue(GcStorage.exist(filePath.orEmpty()))
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
                    runGcsPath = "gcsPath"
                )
            }
        )
    }

    @Test
    fun `should return that file exist`() {
        // given
        GcStorage.upload(File.createTempFile("testFile", ".txt").path, "bucket", "path")

        // when
        val actual = GcStorage.exist(
            "bucket",
            "path"
        )

        // then
        assertTrue(actual)
    }

    @Test
    fun `should return that file does not exist`() {
        // when
        val actual = GcStorage.exist(
            "bucket",
            "path_not_existed"
        )

        // then
        assertFalse(actual)
    }

    @Test
    fun `should return false when file does not exits`() {
        // given
        val actual = GcStorage.exist("gs://not/existed/file.txt")

        // then
        assertFalse(actual)
    }

    @Test
    fun `should return true when file exist`() {
        // given
        val tempFile = File.createTempFile("test", ".txt")
        GcStorage.upload(tempFile.path, "bucket", "path")

        // given
        val actual = GcStorage.exist("gs://bucket/path/${tempFile.name}")

        // then
        assertTrue(actual)

        // clean
        tempFile.delete()
    }
}
