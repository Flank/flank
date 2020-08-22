package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.yml.AppTestPair
import ftl.run.model.InstrumentationTestContext
import ftl.run.exception.FlankGeneralError
import ftl.util.asFileReference
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ResolveApksKtTest {

    @Test
    fun `should resolve apks from global app and test`() {
        assertArrayEquals(
            arrayOf(
                InstrumentationTestContext(
                    app = "app".asFileReference(),
                    test = "test".asFileReference()
                )
            ),
            mockk<AndroidArgs> {
                every { appApk } returns "app"
                every { testApk } returns "test"
                every { additionalApks } returns emptyList()
                every { additionalAppTestApks } returns emptyList()
            }.resolveApks().toTypedArray()
        )
    }

    @Test
    fun `should resolve apks from additionalAppTestApks`() {
        assertArrayEquals(
            arrayOf(
                InstrumentationTestContext(
                    app = "app".asFileReference(),
                    test = "test".asFileReference()
                )
            ),
            mockk<AndroidArgs> {
                every { appApk } returns null
                every { testApk } returns null
                every { additionalApks } returns emptyList()
                every { additionalAppTestApks } returns listOf(
                    AppTestPair(
                        app = "app",
                        test = "test"
                    )
                )
            }.resolveApks().toTypedArray()
        )
    }

    @Test(expected = FlankGeneralError::class)
    fun `should fail on missing app apk`() {
        mockk<AndroidArgs> {
            every { appApk } returns null
            every { testApk } returns null
            every { additionalApks } returns emptyList()
            every { additionalAppTestApks } returns listOf(
                AppTestPair(
                    app = null,
                    test = "test"
                )
            )
        }.resolveApks()
    }
}
